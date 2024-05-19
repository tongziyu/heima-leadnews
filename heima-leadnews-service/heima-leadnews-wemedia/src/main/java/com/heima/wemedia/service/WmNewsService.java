package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/20 00:03
 */
public interface WmNewsService extends IService<WmNews> {
    /**
     * 条件查询文章列表
     * @param dto
     * @return
     */
    ResponseResult findList(WmNewsPageReqDto dto);
}
