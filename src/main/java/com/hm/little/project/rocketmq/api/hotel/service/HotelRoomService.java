package com.hm.little.project.rocketmq.api.hotel.service;

import com.ruyuan.little.project.rocketmq.api.hotel.dto.HotelRoom;

public interface HotelRoomService {

    HotelRoom  getRoomById(Long id, String phoneNumber);

}
