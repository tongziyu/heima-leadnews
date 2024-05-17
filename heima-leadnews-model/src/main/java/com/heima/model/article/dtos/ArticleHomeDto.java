package com.heima.model.article.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/17 10:53
 */
@Data
public class ArticleHomeDto {
    // 最大时间
    private Date maxBehotTime;

    // 最小时间
    private Date minBehotTime;

    // 分页数量
    private Integer size;

    // 频道id
    private String tag;

}
