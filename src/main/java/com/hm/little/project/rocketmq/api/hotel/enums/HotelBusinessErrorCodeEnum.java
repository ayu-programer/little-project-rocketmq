package com.hm.little.project.rocketmq.api.hotel.enums;

/**
 * 酒店业务系统的code码
 */
public enum  HotelBusinessErrorCodeEnum {

    HOTEL_ROOM_NOT_EXIST(580,"酒店房间信息不存在"),
        ;
    private int code;
    private String message;

    HotelBusinessErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
