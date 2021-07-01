package com.hm.little.project.rocketmq.api.coupon.dto;

/**
 * 队列消息实体类
 */
public class FirstLoginMessageDTO {


    /**
     * 用户id
     */
    private Integer userId;


    /**
     * 用户名称
     */
    private String nickName;


    /**
     * 手机号
     */
    private String phoneNumber;



    /**
     * 小程序id
     */
    private Integer beid;


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getBeid() {
        return beid;
    }

    public void setBeid(Integer beid) {
        this.beid = beid;
    }
}
