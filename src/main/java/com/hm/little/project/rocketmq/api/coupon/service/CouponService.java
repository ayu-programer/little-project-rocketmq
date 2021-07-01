package com.hm.little.project.rocketmq.api.coupon.service;

public interface CouponService {

    /**
     *  分发第一次登陆的优惠券
     * @param beId 业务id
     * @param userId  用户id
     * @param couponConfigId  优惠券配置表id
     * @param vaildDays 有效期
     * @param sourceOrderId 业务来源订单id
     * @param phoneNumber 手机号
     */
   void attributeCoupon(Integer beId,
                        Integer userId,
                        Integer couponConfigId,
                        Integer vaildDays,
                        Integer sourceOrderId,
                        String phoneNumber);
}
