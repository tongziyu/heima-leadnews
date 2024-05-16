package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/16 23:01
 */
@Service
@Slf4j
public class ApUserServiceImpl
        extends ServiceImpl<ApUserMapper, ApUser>
        implements ApUserService {

    @Autowired
    private ApUserMapper apUserMapper;

    /**
     * 用户登录
     *
     * @param loginDto
     * @return
     */
    @Override
    public ResponseResult login(LoginDto loginDto) {

        /**
         * 用户登录逻辑:
         *  如果传来的信息中有 手机号和密码,则进行校验登录,登录成功后返回jwt令牌加用户信息
         *  如果传来的信息中没有 手机号和密码,则直接返回 jwt,用0来生成
         *
         *  1.知识点:
         *      1.1 使用org.apache.commons.lang3.StringUtils; 来判断字符串是否为空
         *      1.2 使用org.springframework.util.DigestUtils; 来对密码进行加密
         */

        // 判断传来的信息有没有手机号和密码
        String phone = loginDto.getPhone();
        String password = loginDto.getPassword();

        if (!StringUtils.isBlank(phone) && !StringUtils.isBlank(password)){
            // 有手机号 和 密码,查询用户

            ApUser userInfo = this.getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, phone));

            if (userInfo == null){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
            }

            String salt = userInfo.getSalt();

            String verifyPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes());

            if (verifyPassword.equals(userInfo.getPassword())){

                // 封装返回信息

                String token = AppJwtUtil.getToken(Long.valueOf(userInfo.getId()));

                // 数据脱敏
                userInfo.setSalt("");

                userInfo.setPassword("********");

                Map<String,Object> map = new HashMap<>();

                map.put("user",userInfo);

                map.put("token",token);

                return ResponseResult.okResult(map);
            }else{
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR,"密码错误");
            }
        }else{
            String token = AppJwtUtil.getToken(0L);

            Map<String,Object> map = new HashMap<>();

            map.put("token",token);
            return ResponseResult.okResult(map);
        }

    }
}
