package com.heima.user.controller.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/16 21:49
 */
@RestController("/user")
@Slf4j
public class UserController {

    @GetMapping("/test")
    public String test(){
        System.out.println("test!!!!");
        return "test";
    }
}
