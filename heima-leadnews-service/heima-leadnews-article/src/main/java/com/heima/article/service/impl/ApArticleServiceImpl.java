package com.heima.article.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/17 10:57
 */
@Slf4j
@Service
public class ApArticleServiceImpl
    extends ServiceImpl<ApArticleMapper, ApArticle>
    implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    private static final Integer MAX_PAGE_SIZE = 50;


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
}
