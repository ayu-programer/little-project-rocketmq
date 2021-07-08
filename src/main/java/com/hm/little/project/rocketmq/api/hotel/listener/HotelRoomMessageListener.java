package com.hm.little.project.rocketmq.api.hotel.listener;


import com.alibaba.fastjson.JSON;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.common.enums.ErrorCodeEnum;
import com.ruyuan.little.project.common.enums.LittleProjectTypeEnum;
import com.ruyuan.little.project.redis.api.RedisApi;
import com.ruyuan.little.project.rocketmq.admin.dto.AdminHotelRoom;
import com.ruyuan.little.project.rocketmq.api.hotel.dto.HotelRoom;
import com.ruyuan.little.project.rocketmq.api.hotel.service.impl.HotelRoomCacheManager;
import com.ruyuan.little.project.rocketmq.common.constant.RedisKeyConstant;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;


/**
 * hanmeng
 * version: 1.0
 * Description:房间更新成功的listener
 **/
@Component
public class HotelRoomMessageListener implements MessageListenerConcurrently {


    private static final Logger logger = LoggerFactory.getLogger(HotelRoomMessageListener.class);

    @Reference(version = "1.0.0",
            interfaceClass = RedisApi.class,
            cluster = "failfast")
    private RedisApi redisApi;


    @Autowired
    private HotelRoomCacheManager hotelRoomCacheManager;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

        for (MessageExt messageExt : msgs) {
            //1 将byte转string
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            try {
                //2 从redis中获取hotelroom信息
                AdminHotelRoom hotelRoom = JSON.parseObject(body, AdminHotelRoom.class);
                Long roomId = hotelRoom.getId();

                logger.info("start query hotel room from redis cache param:{}", roomId);
                CommonResponse<String> response = redisApi.get(
                        RedisKeyConstant.HOTEL_ROOM_KEY_PREFIX + roomId,
                        hotelRoom.getPhoneNumber(),
                        LittleProjectTypeEnum.ROCKETMQ);
                logger.info("end query hotel room from redis cache param:{}", roomId);
                if (Objects.equals(response.getCode(), ErrorCodeEnum.SUCCESS)) {
                    logger.info("update hotel room local cache data:{}", response.getData());
                    //3 更新本地缓存信息
                    hotelRoomCacheManager.updateLocalCache(JSON.parseObject(response.getData(), HotelRoom.class));
                }

            } catch (Exception e) {
              logger.error("从mq中获取消息失败 参数 {}",body);
              return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
