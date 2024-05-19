package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/19 21:04
 */

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 上传图片信息到minio中
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 查询素材列表
     * @param dto
     * @return
     */
    ResponseResult findList(WmMaterialDto dto);
}
