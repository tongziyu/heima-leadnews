package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/19 23:41
 */
public interface WmChannelService extends IService<WmChannel> {
    /**
     * 获取频道列表
     * @return
     */
    ResponseResult getChannels();
}
