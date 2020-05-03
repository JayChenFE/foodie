package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.vo.UserVO;
import com.github.jaychenfe.service.UserService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.JsonUtils;
import com.github.jaychenfe.utils.Md5Utils;
import com.github.jaychenfe.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.stream.Stream;


/**
 * @author jaychenfe
 */
@Controller
public class SsoController {

    private final UserService userService;

    private final RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "user_token";
    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";
    public static final String REDIS_USER_TICKET = "user_ticket";
    public static final String REDIS_TMP_TICKET = "tmp_ticket";

    @Autowired
    public SsoController(UserService userService, RedisOperator redisOperator) {
        this.userService = userService;
        this.redisOperator = redisOperator;
    }

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        model.addAttribute("returnUrl", returnUrl);

        // 1. 获取userTicket门票，如果cookie中能够获取到，证明用户登录过，此时签发一个一次性的临时票据并且回跳
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        if (verifyUserTicket(userTicket)) {
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        model.addAttribute("returnUrl", returnUrl);

        //0.判断用户名密码必须不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {

            model.addAttribute("errmsg", "用户名或者密码不能为空");
            return "login";
        }
        //1.实现注册
        Users userResult = userService.queryUserForLogin(username,
                Md5Utils.getMd5Str(password));

        if (null == userResult) {
            model.addAttribute("errmsg", "用户名或者密码不正确");
            return "login";
        }

        // 2.生成用户token，存入redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UserVO usersVO = new UserVO();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(),
                JsonUtils.objectToJson(usersVO));

        // 3.生成ticket门票， 全局门票。代表用户在CAS登陆过
        String userTicket = UUID.randomUUID().toString().trim();

        //用户全局门票需要放入CAS端的cookie中,这样当下次再次登录时，可以通过cookie检测是否已经登陆过cas
        setCookie(COOKIE_USER_TICKET, userTicket, response);

        // 4.userTicket关联用户ID ，并且放入到redis中，代表这个用户有门票了
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, userResult.getId());

        //5.生成临时票据，回跳到调用端网址，是由CAS端所签发的的一个一次性的临时票据
        String tmpTicket = createTmpTicket();

        /**
         *  userTicket : 用于表示用户在CAS端的一个登陆状态，已经登陆
         *  tmpTicket: 用于颁发给用户进行一次性验证的票据，有时效性
         *
         */

        // 回跳
        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public ApiResponse verifyTmpTicket(String tmpTicket,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        // 使用一次性临时票据来验证用户是否登录，如果登录过，把用户会话信息返回给站点
        // 使用完毕后，需要销毁临时票据
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            return ApiResponse.errorUserTicket("用户票据异常");
        }

        // 0. 如果临时票据OK，则需要销毁，并且拿到CAS端cookie中的全局userTicket，以此再获取用户会话
        if (!tmpTicketValue.equals(Md5Utils.getMd5Str(tmpTicket))) {
            return ApiResponse.errorUserTicket("用户票据异常");
        } else {
            // 销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }
        // 1. 验证并且获取用户的userTicket
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return ApiResponse.errorUserTicket("用户票据异常");
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return ApiResponse.errorUserTicket("用户票据异常");
        }
        // 验证成功，返回OK，携带用户会话
        return ApiResponse.ok(JsonUtils.jsonToPojo(userRedis, UserVO.class));
    }

    @PostMapping("/logout")
    @ResponseBody
    public ApiResponse logout(String userId,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        // 0. 获取CAS中的用户门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        // 1. 清除 userTicket票据，redis/cookie
        delCookie(COOKIE_USER_TICKET, response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);
        // 2. 清除用户全局会话（分布式会话）
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        return ApiResponse.ok();

    }

    /**
     * 生成临时票据
     *
     * @return 临时票据
     */
    private String createTmpTicket() {
        String tmpTicket = UUID.randomUUID().toString().trim();
        //有过期时间
        try {
            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket,
                    Md5Utils.getMd5Str(tmpTicket), 600);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tmpTicket;
    }

    /**
     * 确认用户票据
     *
     * @param userTicket 用户票据
     * @return 确认是否通过
     */
    private boolean verifyUserTicket(String userTicket) {
        // 0. 验证CAS门票不能为空
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        return !StringUtils.isBlank(userRedis);
    }


    private String getCookie(HttpServletRequest request,
                             String key) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies || StringUtils.isBlank(key)) {
            return null;
        }

        return Stream.of(cookies)
                .filter(x -> x.getName().equals(key))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

    }

    private void setCookie(String key,
                           String val,
                           HttpServletResponse response) {

        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    private void delCookie(String key,
                           HttpServletResponse response) {

        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}
