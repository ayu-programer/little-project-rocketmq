package com.hm.little.project.rocketmq.api.hotel.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruyuan.little.project.rocketmq.api.hotel.dto.HotelRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * jvm本地缓存
 */
@Component
public class HotelRoomCacheManager {

    private Logger logger = LoggerFactory.getLogger(HotelRoomCacheManager.class);


    //这里可以TODO 防止oom可以通过google Guava Cache改造
    private Map<Long, HotelRoom> hotelRoomMap = new HashMap<Long,HotelRoom>();


    /**
     * 获取房间信息
     * @param roomId
     * @return
     */
    public HotelRoom getHotelRoomFromLocalCache(Long roomId){
       return hotelRoomMap.get(roomId);
    }


    /**
     * 更新本地缓存信息
     * @param hotelRoom
     */
    public void updateLocalCache(HotelRoom hotelRoom){
        hotelRoomMap.put(hotelRoom.getId(),hotelRoom);

        logger.info("hotel room local cache data {}", JSON.toJSONString(hotelRoom));
    }
}
