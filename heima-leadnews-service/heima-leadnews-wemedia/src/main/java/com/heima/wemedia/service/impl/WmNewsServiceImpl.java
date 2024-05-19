package com.heima.wemedia.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/20 00:04
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews>
                                implements WmNewsService {

    /**
     * 条件查询文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {
        // 1. 校验分页参数
        dto.checkParam();

        // 封装分页数据
        IPage ipage = new Page(dto.getPage(),dto.getSize());


        // 2. 设置条件
        LambdaQueryWrapper<WmNews> wrapper = new LambdaQueryWrapper<>();
        // 2.1 根据状态精确查询
        if (dto.getStatus() != null) {
            wrapper.eq(WmNews::getStatus,dto.getStatus());
        }
        // 2.2 根据频道精确查询
        if (dto.getChannelId() != null){
            wrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }
        // 2.3 查询当前登录人的文章
        Integer UserId = WmThreadLocalUtil.getUser().getId();
        wrapper.eq(WmNews::getUserId,UserId);

        // 2.4 时间范围查询
        if (dto.getEndPubDate() != null && dto.getBeginPubDate() != null){
            wrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }

        // 2.5 根据keyword 模糊查询
        if (StrUtil.isNotBlank(dto.getKeyword())){
            wrapper.like(WmNews::getTitle,dto.getKeyword());
        }

        // 2.6 按照发布时间倒叙查询
        wrapper.orderByDesc(WmNews::getPublishTime);

        // 进行分页查询
        IPage<WmNews> page = this.page(ipage, wrapper);

        // 3. 包装返回结果
        PageResponseResult result = new PageResponseResult(dto.getPage(),dto.getSize(), (int) page.getTotal());

        result.setData(page.getRecords());
        // 4. 返回结果
        return result;
        /*// 1. 校验分页参数
        dto.checkParam();

        // 封装分页数据
        IPage ipage = new Page(dto.getPage(),dto.getSize());


        // 2. 设置条件
        LambdaQueryWrapper<WmNews> wrapper = new LambdaQueryWrapper<>();
        // 2.1 根据状态精确查询
        if (dto.getStatus() != null) {
            wrapper.eq(WmNews::getStatus,dto.getStatus());
        }
        // 2.2 根据频道精确查询
        if (dto.getChannelId() != null){
            wrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }
        // 2.3 查询当前登录人的文章
        Integer UserId = WmThreadLocalUtil.getUser().getId();
        wrapper.eq(WmNews::getUserId,UserId);

        // 2.4 时间范围查询
        if (dto.getEndPubDate() != null && dto.getBeginPubDate() != null){
            wrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }

        // 2.5 根据keyword 模糊查询
        if (StrUtil.isNotBlank(dto.getKeyword())){
            wrapper.like(WmNews::getTitle,dto.getKeyword());
        }

        // 2.6 按照发布时间倒叙查询
        wrapper.orderByDesc(WmNews::getPublishTime);

        // 进行分页查询
        IPage<WmNews> page = this.page(ipage, wrapper);

        // 3. 包装返回结果
        PageResponseResult result = new PageResponseResult(dto.getPage(),dto.getSize(), (int) page.getTotal());

        result.setData(page.getRecords());
        // 4. 返回结果
        return result;*/

    }
}
