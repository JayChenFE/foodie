package com.github.jaychenfe.controller.interceptor;

import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.JsonUtils;
import com.github.jaychenfe.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author jaychenfe
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisOperator redisOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(userToken)) {
            writeErrorToResponse(response, ApiResponse.errorMsg("请登录"));
            return false;
        }

        String redisKey = "user_token:" + userId;
        String userTokenInRedis = redisOperator.get(redisKey);

        if (StringUtils.isBlank(userTokenInRedis)) {
            writeErrorToResponse(response, ApiResponse.errorMsg("请登录"));
            return false;
        }

        if (!userTokenInRedis.equals(userToken)) {
            writeErrorToResponse(response, ApiResponse.errorMsg("账号异地登录"));
            return false;
        }

        return true;
    }

    private void writeErrorToResponse(HttpServletResponse response, ApiResponse apiResponse) {
        OutputStream os = null;

        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        try {
            os = response.getOutputStream();
            os.write(Objects.requireNonNull(JsonUtils.objectToJson(apiResponse)).getBytes(StandardCharsets.UTF_8));
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
