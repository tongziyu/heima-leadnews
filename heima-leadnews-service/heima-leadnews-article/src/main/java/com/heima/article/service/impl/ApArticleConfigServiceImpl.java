package com.heima.article.service.impl;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/22 22:10
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.model.article.pojos.ApArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ApArticleConfigServiceImpl
        extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig>
        implements ApArticleConfigService {
}
