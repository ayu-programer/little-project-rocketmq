package com.hm.little.project.rocketmq.admin.dto;

/**
 * @author <a href="mailto:little@163.com">little</a>
 * version: 1.0
 * Description:酒店房间更新消息
 **/
public class AdminHotelRoomMessage {

    /**
     * 房间id
     */
    private Long roomId;

    /**
     * 手机号
     */
    private String phoneNumber;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}