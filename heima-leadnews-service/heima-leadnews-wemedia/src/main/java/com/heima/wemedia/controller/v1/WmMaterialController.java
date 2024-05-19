package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/19 20:55
 */
@RestController
@RequestMapping("/api/v1/material")
@Slf4j
public class WmMaterialController {
    @Autowired
    private WmMaterialService wmMaterialService;


    /**
     * 文件上传到Minio
     * @param multipartFile
     * @return
     *      - 上传成功,返回图片的url信息,用于回显
     *      - 上传失败,返回501,message:文件上传失败
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(@RequestParam("multipartFile") MultipartFile multipartFile){


        return wmMaterialService.uploadPicture(multipartFile);
    }
}
