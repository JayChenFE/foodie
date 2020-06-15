package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.vo.UserVO;
import com.github.jaychenfe.resourse.FileResource;
import com.github.jaychenfe.service.FdfsService;
import com.github.jaychenfe.service.center.CenterUserService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.CookieUtils;
import com.github.jaychenfe.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestController
@RequestMapping("fdfs")
public class CenterUserController extends BaseController {

    @Autowired
    private FileResource fileResource;

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FdfsService fdfsService;

    private static final String[] IMAGE_PREFIX_ARR = new String[]{"png", "jpg", "jpeg"};


    @PostMapping("uploadFace")
    public ApiResponse uploadFace(
            String userId,
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String path = "";
        // 开始文件上传
        if (file == null) {
            return ApiResponse.errorMsg("文件不能为空！");
        }

        // 获得文件上传的文件名称
        String fileName = file.getOriginalFilename();
        if (StringUtils.isNotBlank(fileName)) {


            // 文件重命名  imooc-face.png -> ["imooc-face", "png"]
            String[] fileNameArr = fileName.split("\\.");

            // 获取文件的后缀名
            String suffix = fileNameArr[fileNameArr.length - 1];

            if (!isImageSuffixOk(suffix)) {
                return ApiResponse.errorMsg("图片格式不正确！");
            }


            path = fdfsService.uploadOSS(file, userId, suffix);
            System.out.println(path);
        }


        if (StringUtils.isBlank(path)) {
            return ApiResponse.errorMsg("上传头像失败");
        }
        // String finalUserFaceUrl = fileResource.getHost() + path;
        String finalUserFaceUrl = fileResource.getOssHost() + path;

        Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);

        UserVO usersVO = conventUsersVO(userResult);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        return ApiResponse.ok();
    }

    private static boolean isImageSuffixOk(String suffix) {
        return Arrays.asList(IMAGE_PREFIX_ARR).contains(suffix);
    }

}
