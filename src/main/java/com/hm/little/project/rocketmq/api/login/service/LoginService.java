package com.hm.little.project.rocketmq.api.login.service;


import com.hm.little.project.rocketmq.api.login.dto.LoginRequestDTO;

public interface LoginService {

    /**
     * 第一次登录赠送优惠券
     * @param loginRequestDTO
     * @return
     */
    void firstLoginDistributeCoupon(LoginRequestDTO loginRequestDTO);

    /**
     * 根据手机号进行更新状态
     * @param phoneNumber
     */
    void resetFirstLoginStatus(String phoneNumber);
}
