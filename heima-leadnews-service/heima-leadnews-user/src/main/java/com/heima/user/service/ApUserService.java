package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/16 23:00
 */
public interface ApUserService extends IService<ApUser> {

    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    ResponseResult login(LoginDto loginDto);
}
