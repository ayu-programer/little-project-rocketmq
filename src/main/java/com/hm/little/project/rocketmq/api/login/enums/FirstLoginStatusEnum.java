package com.hm.little.project.rocketmq.api.login.enums;

/**
 * 用户登录枚举类
 */
public enum FirstLoginStatusEnum {
    YES(1, "未登陆过"),
    NO(2, "已登录过");

    private int status;

    private String desc;

    FirstLoginStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
