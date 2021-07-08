package com.hm.little.project.rocketmq.admin.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//即让项目启动的时候就将这个hotelRoomProducer bean注入到容器中
@Configuration
public class AdminHotelRoomConfiguration {


    @Value("${rocketmq.namesrv.address}")
    private String namesrvAddress;


    @Value("${rocketmq.hotelRoom.producer.group}")
    private String hotelRoomGroup;


    @Bean("hotelRoomProducer")
    public DefaultMQProducer hotelRoomProducer() throws MQClientException {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(hotelRoomGroup);
        defaultMQProducer.setNamesrvAddr(namesrvAddress);
        defaultMQProducer.start();
        return defaultMQProducer;
    }
}
