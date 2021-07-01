package com.hm.little.project.rocketmq.api.login.controller;

import com.alibaba.fastjson.JSON;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.rocketmq.api.login.dto.LoginRequestDTO;
import com.ruyuan.little.project.rocketmq.api.login.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {


    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    private LoginService loginService;

    public CommonResponse appletLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        logger.info("成功登录的 用户信息：{}", JSON.toJSONString(loginRequestDTO));

        //调用登录service
        loginService.firstLoginDistributeCoupon(loginRequestDTO);

        return CommonResponse.success();
    }

    public CommonResponse resetFirstLoginStatus(@RequestParam("phoneNumber") String phoneNumber){
        logger.info("登录用户的手机号 {}",phoneNumber);
        loginService.resetFirstLoginStatus(phoneNumber);

        return CommonResponse.success();
    }
}
