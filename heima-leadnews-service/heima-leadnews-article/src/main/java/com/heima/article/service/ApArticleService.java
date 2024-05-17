package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/17 10:56
 */

public interface ApArticleService extends IService<ApArticle> {
    /**
     * 加载文章首页
     * @param articleHomeDto
     * @return
     */
    ResponseResult load(ArticleHomeDto articleHomeDto, Short type);
}
