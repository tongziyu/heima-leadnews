package com.heima.article.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleFreemarkerService;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/17 10:57
 */
@Slf4j
@Service
@Transactional
public class ApArticleServiceImpl
    extends ServiceImpl<ApArticleMapper, ApArticle>
    implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    private static final Integer MAX_PAGE_SIZE = 50;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApArticleFreemarkerService apArticleFreemarkerService;


    /**
     * 加载文章
     *
     * @param articleHomeDto
     * @return
     */
    @Override
    public ResponseResult load(ArticleHomeDto articleHomeDto, Short type) {
        /**
         * 加载文章步骤
         * 1. 分页条数的校验
         *      - 如果分页条数是空,则给赋值一个10
         *      - 如果分页条数不是空,则要小于50
         * 2. 校验参数
         *      - 校验type是1还是2,判断是加载更多还是加载最新
         * 3. 频道参数校验
         *      - 校验频道,如果频道参数是空,则赋值 __all__
         * 4. 时间校验
         *      - 判断如果最大时间是null的话,将最大时间设置成现在
         *      - 判断如果最小时间是null的话,将最小时间设置成现在
         *   查询
         *   返回结果
         */

        if (articleHomeDto.getSize() == null || articleHomeDto.getSize() == 0){
            articleHomeDto.setSize(10);
        }

        articleHomeDto.setSize(Math.min(articleHomeDto.getSize(), MAX_PAGE_SIZE));

        // 如果不是加载更多,或者加载最新,则给赋值为加载更多
        if (!ArticleConstants.LOADTYPE_LOAD_MORE.equals(type) && !ArticleConstants.LOADTYPE_LOAD_NEW.equals(type)){
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        // 频道校验
        if (StrUtil.isBlank(articleHomeDto.getTag())){
            articleHomeDto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        // 校验时间
        if (articleHomeDto.getMaxBehotTime() == null){
            articleHomeDto.setMaxBehotTime(new Date());
        }
        if (articleHomeDto.getMinBehotTime() == null){
            articleHomeDto.setMinBehotTime(new Date());
        }
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(type, articleHomeDto);

        return ResponseResult.okResult(apArticles);
    }

    /**
     * 保存文章
     *
     * @param articleDto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto articleDto) {

        // 1. 参数校验
        if (articleDto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();

        BeanUtils.copyProperties(articleDto, apArticle);
        // 保存或者修改文章
        if (articleDto.getId() == null){
            // 保存article
            save(apArticle);
            log.info("保存的article信息:{}",apArticle);

            // 保存article Config
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setArticleId(apArticle.getId());
            apArticleConfig.setIsDown(false);
            apArticleConfig.setIsDelete(false);
            apArticleConfig.setIsComment(true);
            apArticleConfig.setIsForward(true);
            apArticleConfigMapper.insert(apArticleConfig);
            log.info("保存的articleConfig信息:{}",apArticleConfig);

            // 保存article Content
            ApArticleContent apArticleContent = new ApArticleContent();

            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(articleDto.getContent());
            apArticleContentMapper.insert(apArticleContent);
            log.info("保存的文章内容:{}",apArticleContent );

        }else{
            // 修改文章
            updateById(apArticle);

            // 修改内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                    .eq(ApArticleContent::getArticleId, apArticle.getId()));

            apArticleContent.setContent(articleDto.getContent());

            apArticleContentMapper.updateById(apArticleContent);

        }

        // 生成静态文件,并保存到minio中

        apArticleFreemarkerService.buildArticleToHtml(apArticle,articleDto.getContent());
        return ResponseResult.okResult(apArticle.getId());
    }
}
