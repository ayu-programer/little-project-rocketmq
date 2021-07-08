package com.hm.little.project.rocketmq.api.hotel.consumer;

import com.ruyuan.little.project.rocketmq.api.hotel.listener.HotelRoomMessageListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消费者配置类
 */
@Configuration
public class HotelRoomConsumerConfiguration {

    @Value("${rocketmq.namesrv.address}")
    private String nameServerAddr;


    @Value("${rocketmq.hotelRoom.topic}")
    private String topic;

    @Value("${rocketmq.hotelRoom.consumer.group}")
    private String hotelRoomConsumerGroup;


    @Bean("loginConsumer")
    public DefaultMQPushConsumer loginConsumer(@Qualifier(value = "hotelRoomMessageListener") HotelRoomMessageListener hotelRoomMessageListener) throws MQClientException {
        //定义消费者
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
        //设置消费者组
        defaultMQPushConsumer.setConsumerGroup(hotelRoomConsumerGroup);
        //为消费者设置nameserver
        defaultMQPushConsumer.setNamesrvAddr(nameServerAddr);
        //设置topic
        defaultMQPushConsumer.subscribe(topic,"*");
        //设置监听器
        defaultMQPushConsumer.setMessageListener(hotelRoomMessageListener);
        defaultMQPushConsumer.start();
        return defaultMQPushConsumer;
    }

}
