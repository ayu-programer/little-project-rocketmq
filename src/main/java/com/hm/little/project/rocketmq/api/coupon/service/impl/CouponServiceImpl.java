package com.hm.little.project.rocketmq.api.coupon.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.common.enums.LittleProjectTypeEnum;
import com.ruyuan.little.project.mysql.api.MysqlApi;
import com.ruyuan.little.project.mysql.dto.MysqlRequestDTO;
import com.ruyuan.little.project.rocketmq.api.coupon.service.CouponService;
import com.ruyuan.little.project.rocketmq.common.utils.DateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    /**
     * 日志组件
     */
    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);



    /**
     * mysql dubbo api接口服务
     */
    @Reference(version = "1.0.0",
            interfaceClass = MysqlApi.class,
            cluster = "failfast")
    private MysqlApi mysqlApi;

    /**
     *  分发第一次登陆的优惠券
     * @param beId 业务id
     * @param userId  用户id
     * @param couponConfigId  优惠券配置表id
     * @param vaildDays 有效期
     * @param sourceOrderId 业务来源订单id
     * @param phoneNumber 手机号
     */
    @Override
    public void attributeCoupon(Integer beId,
                                Integer userId,
                                Integer couponConfigId,
                                Integer vaildDays,
                                Integer sourceOrderId,
                                String phoneNumber) {

        MysqlRequestDTO mysqlRequestDTO = new MysqlRequestDTO();
        mysqlRequestDTO.setSql("insert into t_coupon_user(" +
                                        "beid," +
                                        "uid," +
                                        "coupon_id," +
                                        "begin_date," +
                                        "end_date," +
                                        "source_order_id," +
                                        "phone_number) " +
                                    "VALUES " +
                                        "(" +
                                        "?," +
                                        "?," +
                                        "?," +
                                        "?," +
                                        "?," +
                                        "?,?)");


        List<Object> params = new ArrayList<>();
        params.add(beId);
        params.add(userId);
        params.add(couponConfigId);
        Date date = new Date();
        params.add(DateUtil.getDateFormat(date,DateUtil.Y_M_D_PATTERN));
        params.add(DateUtil.getDateFormat(DateUtils.addDays(date,vaildDays),DateUtil.Y_M_D_PATTERN));
        params.add(sourceOrderId);
        params.add(phoneNumber);
        mysqlRequestDTO.setParams(params);
        mysqlRequestDTO.setPhoneNumber(phoneNumber);
        mysqlRequestDTO.setProjectTypeEnum(LittleProjectTypeEnum.ROCKETMQ);

        logger.info("插入前的数据 {}", JSON.toJSONString(mysqlRequestDTO));
        CommonResponse<Integer> insertCoupon = mysqlApi.insert(mysqlRequestDTO);

        logger.info("保存优惠券信息后的返回结果  {}",JSON.toJSONString(insertCoupon));
    }
}
