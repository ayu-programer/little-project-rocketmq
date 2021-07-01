package com.hm.little.project.rocketmq.api.coupon.consumer;


import com.hm.little.project.rocketmq.api.coupon.listener.FirstLoginMessageListener;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义优惠券的消费者
 */
@Configuration
public class CouponConsumerConfiguration {


    @Value("${rocketmq.namesrv.address}")
    private String nameServerAddr;


    @Value("${rocketmq.login.topic}")
    private String topic;

    @Value("${rocketmq.login.consumer.group}")
    private String loginConsumerGroup;


    @Bean("loginConsumer")
    public DefaultMQPushConsumer loginConsumer(@Qualifier(value = "firstLoginMessageListener") FirstLoginMessageListener firstLoginMessageListener) throws MQClientException {
        //定义消费者
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
        //设置消费者组
        defaultMQPushConsumer.setConsumerGroup(loginConsumerGroup);
        //为消费者设置nameserver
        defaultMQPushConsumer.setNamesrvAddr(nameServerAddr);
        //设置topic
        defaultMQPushConsumer.subscribe(topic,"*");
        //设置监听器
        defaultMQPushConsumer.setMessageListener(firstLoginMessageListener);
        defaultMQPushConsumer.start();
        return defaultMQPushConsumer;
    }
}
