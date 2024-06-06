package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/23 18:14
 */
public interface ApArticleFreemarkerService {
    /**
     * 生成静态文件上传到minio中
     * @param apArticle
     * @param content
     */
    void buildArticleToHtml(ApArticle apArticle,String content);
}
