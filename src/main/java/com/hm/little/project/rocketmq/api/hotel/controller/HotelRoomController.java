package com.hm.little.project.rocketmq.api.hotel.controller;

import com.hm.little.project.rocketmq.api.hotel.dto.HotelRoom;
import com.hm.little.project.rocketmq.api.hotel.service.HotelRoomService;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.rocketmq.api.hotel.dto.HotelRoom;
import com.ruyuan.little.project.rocketmq.api.hotel.service.HotelRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotel")
public class HotelRoomController {

    @Autowired
    private HotelRoomService hotelRoomService;

    @RequestMapping("getRoomById")
    public ResponseBody getRoomById(@RequestParam("id") Long  id, @RequestParam("phoneNumber")String phoneNumber){
        HotelRoom hotelRooms = hotelRoomService.getRoomById(id, phoneNumber);
        return (ResponseBody) CommonResponse.success(hotelRooms);
    }
}
