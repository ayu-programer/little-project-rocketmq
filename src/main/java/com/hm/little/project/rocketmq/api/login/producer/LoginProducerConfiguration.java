package com.hm.little.project.rocketmq.api.login.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginProducerConfiguration {

    @Value("${rocketmq.namesrv.address}")
    private String namesrvAddress;

    @Value("${rocketmq.login.producer.group}")
    private String loginProducerGroup;


    @Bean(value = "loginMqProducer")
    public DefaultMQProducer logMqProducer() throws MQClientException {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(loginProducerGroup);
        defaultMQProducer.setNamesrvAddr(namesrvAddress);
        defaultMQProducer.start();
        return defaultMQProducer;
    }
}
