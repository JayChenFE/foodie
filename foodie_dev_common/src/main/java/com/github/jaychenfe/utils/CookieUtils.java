package com.github.jaychenfe.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.stream.Stream;


/**
 * @author jaychenfe
 * @version V1.0
 * @Title: CookieUtils.java
 * @Description: Cookie 工具类
 */
public final class CookieUtils {

    static final Logger logger = LoggerFactory.getLogger(CookieUtils.class);

    /**
     * @param request    HttpServletRequest
     * @param cookieName 键
     * @return 值
     * @Description: 得到Cookie的值, 不编码
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, false);
    }

    /**
     * @param request    HttpServletRequest
     * @param cookieName 键
     * @param isDecoder  是否编码
     * @return 值
     * @Description: 得到Cookie的值
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals(cookieName)) {
                    if (isDecoder) {
                        retValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } else {
                        retValue = cookie.getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * @param request      request
     * @param cookieName   cookieName
     * @param encodeString encodeString
     * @return 得到Cookie的值
     * @Description: 得到Cookie的值
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, String encodeString) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals(cookieName)) {
                    retValue = URLDecoder.decode(cookie.getValue(), encodeString);
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * @param request     request
     * @param response    response
     * @param cookieName  cookieName
     * @param cookieValue cookieValue
     * @Description: 设置Cookie的值 不设置生效时间默认浏览器关闭即失效,也不编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1);
    }

    /**
     * @param request      request
     * @param response     response
     * @param cookieName   cookieName
     * @param cookieValue  cookieValue
     * @param cookieMaxAge cookie最大时间
     * @Description: 设置Cookie的值 在指定时间内生效,但不编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxAge) {
        setCookie(request, response, cookieName, cookieValue, cookieMaxAge, false);
    }

    /**
     * @param request     request
     * @param response    response
     * @param cookieName  cookieName
     * @param cookieValue cookieValue
     * @param isEncode    是否编码
     * @Description: 设置Cookie的值 不设置生效时间,但编码
     * 在服务器被创建，返回给客户端，并且保存客户端
     * 如果设置了SETMAXAGE(int seconds)，会把cookie保存在客户端的硬盘中
     * 如果没有设置，会默认把cookie保存在浏览器的内存中
     * 一旦设置setPath()：只能通过设置的路径才能获取到当前的cookie信息
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, boolean isEncode) {
        setCookie(request, response, cookieName, cookieValue, -1, isEncode);
    }

    /**
     * @param request      request
     * @param response     response
     * @param cookieName   cookieName
     * @param cookieValue  cookieValue
     * @param cookieMaxAge cookie最大时间
     * @param isEncode     是否编码
     * @Description: 设置Cookie的值 在指定时间内生效, 编码参数
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxAge, boolean isEncode) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxAge, isEncode);
    }

    /**
     * @param request      request
     * @param response     response
     * @param cookieName   cookieName
     * @param cookieValue  cookieValue
     * @param cookieMaxAge cookie最大时间
     * @param encodeString encodeString
     * @Description: 设置Cookie的值 在指定时间内生效, 编码参数(指定编码)
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxAge, String encodeString) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxAge, encodeString);
    }

    /**
     * @param request    request
     * @param response   response
     * @param cookieName cookieName
     * @Description: 删除Cookie带cookie域名
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response,
                                    String cookieName) {
        doSetCookie(request, response, cookieName, null, -1, false);
    }


    private static void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                                    String cookieName, String cookieValue, int cookieMaxage, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            addCookieToResponse(request, response, cookieName, cookieValue, cookieMaxage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                                    String cookieName, String cookieValue, int cookieMaxAge, String encodeString) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else {
                cookieValue = URLEncoder.encode(cookieValue, encodeString);
            }
            addCookieToResponse(request, response, cookieName, cookieValue, cookieMaxAge);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addCookieToResponse(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        if (cookieMaxage > 0) {
            cookie.setMaxAge(cookieMaxage);
        }
        // 设置域名的cookie
        if (null != request) {
            String domainName = getDomainName(request);
            logger.info("========== domainName: {} ==========", domainName);
            if (!"localhost".equals(domainName)) {
                cookie.setDomain(domainName);
            }
        }
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * @param request request
     * @return cookie的域名
     * @Description: 得到cookie的域名
     */
    private static String getDomainName(HttpServletRequest request) {
        String domainName;

        String serverName = request.getRequestURL().toString();
        if ("".equals(serverName)) {
            return "";
        }
        serverName = serverName.toLowerCase().substring(7);
        final int end = serverName.indexOf("/");
        serverName = serverName.substring(0, end);
        if (serverName.indexOf(":") > 0) {
            String[] ary = serverName.split(":");
            serverName = ary[0];
        }

        final String[] domains = serverName.split("\\.");
        int len = domains.length;
        if (len > 3 && !isIp(serverName)) {
            // www.xxx.com.cn
            domainName = "." + domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
        } else if (len <= 3 && len > 1) {
            // xxx.com or xxx.cn
            domainName = "." + domains[len - 2] + "." + domains[len - 1];
        } else {
            domainName = serverName;
        }
        return domainName;
    }

    public static String trimSpaces(String ip) {//去掉IP字符串前后所有的空格
        while (ip.startsWith(" ")) {
            ip = ip.substring(1).trim();
        }
        while (ip.endsWith(" ")) {
            ip = ip.substring(0, ip.length() - 1).trim();
        }
        return ip;
    }

    public static boolean isIp(String ip) {//判断是否是一个IP
        boolean b = false;
        ip = trimSpaces(ip);
        if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String[] s = ip.split("\\.");
            b = Stream.of(s).allMatch(x -> Integer.parseInt(x) < 255);
        }
        return b;
    }

}
