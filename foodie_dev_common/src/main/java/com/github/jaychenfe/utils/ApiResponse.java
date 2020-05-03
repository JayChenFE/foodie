package com.github.jaychenfe.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

/**
 * @author jaychenfe
 */
public class ApiResponse {
    /**
     * 定义jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 响应业务状态
     */
    private Integer status;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应中的数据
     */
    private Object data;

    /**
     * 不使用
     */

    @JsonIgnore
    private String ok;

    public static ApiResponse build(Integer status, String msg, Object data) {
        return new ApiResponse(status, msg, data);
    }

    public static ApiResponse build(Integer status, String msg, Object data, String ok) {
        return new ApiResponse(status, msg, data, ok);
    }

    public static ApiResponse ok(Object data) {
        return new ApiResponse(data);
    }

    public static ApiResponse ok() {
        return new ApiResponse(null);
    }

    public static ApiResponse errorMsg(String msg) {
        return new ApiResponse(500, msg, null);
    }

    public static ApiResponse errorMap(Object data) {
        return new ApiResponse(501, "error", data);
    }

    public static ApiResponse errorTokenMsg(String msg) {
        return new ApiResponse(502, msg, null);
    }

    public static ApiResponse errorException(String msg) {
        return new ApiResponse(555, msg, null);
    }

    public static ApiResponse errorUserQq(String msg) {
        return new ApiResponse(556, msg, null);
    }

    public static ApiResponse errorUserTicket(String msg) {
        return new ApiResponse(557, msg, null);
    }

    public ApiResponse() {

    }

    public ApiResponse(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ApiResponse(Integer status, String msg, Object data, String ok) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.ok = ok;
    }

    public ApiResponse(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOk() {
        return this.status == HttpStatus.OK.value();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }
}
