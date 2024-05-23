package com.heima.wemedia.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/20 00:04
 */
@Service
@Slf4j
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews>
                                implements WmNewsService {
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;


    /**
     * 条件查询文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {
        /*
        随便写点:
            开发中的注意事项:
                1. 对参数的校验一定要敏感, 比如分页参数, 查询条件等
                2. 要深刻理解面向对象思想
                3. 多用工具类,比如 StrUtil BeanUtil 熟练掌握里面的重要方法
                4. 对MP的分页一定要熟悉,先创建一个Ipage对象,然后通过page() 方法,
                                    第一个参数传入page对象,第二个参数可以传入wrapper条件.
         */
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
    }

    /**
     * 发布文章的公共方法
     * 该功能为保存,修改(是否有id),保存草稿的共有方法
     *
     * 当用户点击提交或者保存草稿的时候,判断是否已经存在文章id
     *  - 如果不存在则新增文章,如果不是保存草稿,则将内容中的图片和素材关联到一起,同时关联封面中的图片与素材的关系
     *      - 如果封面选择的是自动,要按照规则去匹配封面图片
     *  - 如果文章id存在,则删除已经关联的素材关系, 修改文章.
     *
     * @return
     */
    @Override
    public ResponseResult publishNews(WmNewsDto wmNewsDto) {
        log.info("请求参数信息:{}",wmNewsDto);

        // 处理参数
        if (wmNewsDto== null || wmNewsDto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(wmNewsDto,wmNews);

        // 将dto集合里面的图片路径转换成字符串
        if (wmNewsDto.getImages() != null && wmNewsDto.getImages().size() > 0){
            String ImagesStr = StringUtils.join(wmNewsDto.getImages(), ",");
            wmNews.setImages(ImagesStr);
        }
        // 数据库里面字段设置的无符号,不能存负数,当要存负数的时候使用 null来代替
        if (wmNewsDto.getType() == WemediaConstants.WM_NEWS_TYPE_AUTO){
            wmNews.setType(null);
        }

        log.info("文章信息:{}",wmNews);

        this.saveOrUpdateNews(wmNews);

        // 判断是否为草稿,如果是草稿,则结束

        if (wmNewsDto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 不是草稿,则保存文章图片和素材的关系
        List<String> materials = extractUrlInfo(wmNewsDto.getContent());
        log.info("图片url:{}",materials);

        saveRelativeInfoForContent(materials,wmNews.getId());
        // 不是草稿,保存文章封面和图片与素材的关系
        saveRelativeInfoForConvert(wmNewsDto,wmNews,materials);

        // 审核文章
        wmNewsAutoScanService.autoScanWmNews(wmNews.getId());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 按照封面类型设置封面数据
     *   封面类型:
     *      1.如果内容图片大于等于1,并且小于3,则设置为单图  type 1
     *      2.如果内容图片大于等于3,则设置为三图    type 3
     *      3.如果内容图为0,则设置为无图    type 0
     *  保存封面图片与素材的关系
     * @param wmNewsDto
     * @param wmNews
     * @param materials
     */
    private void saveRelativeInfoForConvert(WmNewsDto wmNewsDto, WmNews wmNews, List<String> materials) {
        List<String> images = wmNewsDto.getImages();
        log.info("封面类型设置封面数据:{}",wmNewsDto);
        // 如果封面设置 是自动,则进入判断
        if (wmNewsDto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            if (materials.size() >= 3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());

            }else if (materials.size() >= 1 && materials.size() < 3){
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());

            }else{
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }
            if (images != null && images.size() >= 1){
                wmNews.setImages(StringUtils.join(images,","));
            }
            this.updateById(wmNews);
        }
        // 保存封面图片与素材的关系
        if (images!=null && images.size() >= 1){
            saveRelativeInfo(images,wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }


    }

    /**
     * 提取图片信息
     * @param content
     * @return
     */
    private List<String> extractUrlInfo(String content) {
        List<String> materialImageUrl = new ArrayList<>();

        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")){
                String imgUrl = (String) map.get("value");
                materialImageUrl.add(imgUrl);
            }

        }
        return materialImageUrl;
    }

    /**
     * 保存或者更新文章
     * @param wmNews
     */
    private void saveOrUpdateNews(WmNews wmNews){
        // 判断文章id是否已经存在,如果存在则修改,如果不存在则新建
        Integer id = wmNews.getId();
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1);
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());


        log.info("填充完内容的文章信息:{}",wmNews);

        if (id == null){
            save(wmNews);
        }else{
            // 删除已关联的素材信息
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery()
                    .eq(WmNewsMaterial::getNewsId,id));
            //修改文章内容
            updateById(wmNews);
        }
    }

    /**
     * 保存素材和内容图片之间的关系
     * @param materials
     * @param newsId
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId){
        saveRelativeInfo(materials,newsId,WemediaConstants.WM_CONTENT_REFERENCE);

    }

    /**
     * 保存文章图片与素材的关系到数据库
     * @param materials
     * @param newsId
     * @param type
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        if (materials != null && !materials.isEmpty()){
            List<WmMaterial> dbWmMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery()
                    .in(WmMaterial::getUrl, materials));

            // 如果查询出来的素材数量为0,则抛出异常
            if (dbWmMaterials == null || dbWmMaterials.size() == 0){
                // 手动抛出异常
                throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
            }
            // 如果查询出来的素材数量和 传来的不一致,则抛出异常
            if (dbWmMaterials.size() != materials.size()){
                throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
            }

            List<Integer> dbMaterialIds = dbWmMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            wmNewsMaterialMapper.saveRelations(dbMaterialIds,newsId,type);
        }
    }
}
