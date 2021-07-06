package com.hm.little.project.rocketmq.api.coupon.listener;

import com.alibaba.fastjson.JSON;
import com.hm.little.project.rocketmq.api.coupon.dto.FirstLoginMessageDTO;
import com.hm.little.project.rocketmq.api.coupon.service.CouponService;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.common.enums.ErrorCodeEnum;
import com.ruyuan.little.project.common.enums.LittleProjectTypeEnum;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 用于处理消息的监听者
 */
@Component
public class FirstLoginMessageListener implements MessageListenerConcurrently {

    private static final Logger logger = LoggerFactory.getLogger(FirstLoginMessageListener.class);


    @Autowired
    private CouponService couponService;

    @Value("${first.login.couponId}")
    private Integer couponId;


    @Value("${first.login.coupon.day}")
    private Integer validDays;


    @Reference(version = "1.0.0",
            interfaceClass = RedisApi.class,
            cluster = "failfast")
    private RedisApi redisApi;


    /**
     * 用于消费消息的方法
     * @param messages
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        //遍历并处理消息
        for (MessageExt messageExt :messages) {
            //解析并获取字符串body
            String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            try {
                //打印输出
                logger.info("开始  获取到的message信息 {}", body);
                //将字符转成实体bean
                FirstLoginMessageDTO firstLoginMessageDTO = JSON.parseObject(body, FirstLoginMessageDTO.class);

                Integer beid = firstLoginMessageDTO.getBeid();
                String phoneNumber = firstLoginMessageDTO.getPhoneNumber();
                Integer userId = firstLoginMessageDTO.getUserId();

                //基于redis来保证优惠券幂等
                CommonResponse<Boolean> response = redisApi.setnx(RedisKeyConstant.FIRST_LOGIN_DUPLICATION_KEY_PREFIX + userId,
                        String.valueOf(userId),
                        phoneNumber,
                        LittleProjectTypeEnum.ROCKETMQ);

                //判断结果
                if (Objects.equals(response.getCode(), ErrorCodeEnum.FAIL.getCode())) {
                    logger.info("调用redis方法失败  {}", userId);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                if (Objects.equals(response.getCode(), ErrorCodeEnum.SUCCESS.getCode())
                        && Objects.equals(response.getData(), Boolean.FALSE)) {
                    logger.info("用户已经登录过，无需发送优惠券 {}", userId);

                } else {
                    //调用优惠券的service处理逻辑
                    couponService.attributeCoupon(beid,
                            firstLoginMessageDTO.getUserId(),
                            couponId,
                            validDays,
                            0,
                            phoneNumber);

                    logger.info("结束  用户的手机号为 {}", phoneNumber);
                }

            } catch (Exception e) {
                logger.info("消费失败，消费实体信息 {} ", body);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
