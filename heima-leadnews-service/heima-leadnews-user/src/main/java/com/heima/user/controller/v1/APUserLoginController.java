package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/16 22:26
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
@Api(tags = "用户登录模块")
public class APUserLoginController {

    private final ApUserService apUserService;


    /**
     * 用户登录接口
     * 测试
     * @param loginDto
     * @return
     */
    @ApiOperation("用户登录功能")
    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto loginDto){
        log.info("用户登录信息:{}",loginDto);
        return apUserService.login(loginDto);
    }
}
