package com.github.jaychenfe.exception;

import com.github.jaychenfe.utils.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author jaychenfe
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * 上传文件超过500k，捕获异常：MaxUploadSizeExceededException
     *
     * @param ex 异常
     * @return ApiResponse
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse handlerMaxUploadFile(MaxUploadSizeExceededException ex) {
        return ApiResponse.errorMsg("文件上传大小不能超过500k，请压缩图片或者降低图片质量再上传！");
    }
}
