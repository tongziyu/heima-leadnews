package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/19 23:41
 */
@Service
@Slf4j
public class WmChannelServiceImpl
                    extends ServiceImpl<WmChannelMapper, WmChannel>
                    implements WmChannelService {
    /**
     * 获取频道列表
     *
     * @return
     */
    @Override
    public ResponseResult getChannels() {
        List<WmChannel> list = list();
        return ResponseResult.okResult(list);
    }
}
