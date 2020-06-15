package com.github.jaychenfe.service;

import org.springframework.web.multipart.MultipartFile;

public interface FdfsService {
    String upload(MultipartFile file, String fileExtName) throws Exception;

    String uploadOSS(MultipartFile file, String userId, String fileExtName) throws Exception;
}
