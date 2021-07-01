package com.hm.little.project.rocketmq.api.login.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.common.enums.ErrorCodeEnum;
import com.ruyuan.little.project.common.enums.LittleProjectTypeEnum;
import com.ruyuan.little.project.mysql.api.MysqlApi;
import com.ruyuan.little.project.mysql.dto.MysqlRequestDTO;
import com.ruyuan.little.project.rocketmq.api.login.dto.LoginRequestDTO;
import com.ruyuan.little.project.rocketmq.api.login.enums.FirstLoginStatusEnum;
import com.ruyuan.little.project.rocketmq.api.login.service.LoginService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);



    /**
     * mysql dubbo api接口
     */
    @Reference(version = "1.0.0",
            interfaceClass = MysqlApi.class,
            cluster = "failfast")
    private MysqlApi mysqlApi;



    @Autowired
    @Qualifier(value = "loginMqProducer")
    private DefaultMQProducer loginMqProducer;

    @Value("${rocketmq.login.topic}")
    private String loginTopic;


    @Override
    public void firstLoginDistributeCoupon(LoginRequestDTO loginRequestDTO) {
        //判断是否是第一次登录
        if (!wthrFirstLogin(loginRequestDTO)) {
            logger.info("用户 {}  不是第一次登录；", loginRequestDTO.getNickName());
            return;
        }
        //更改用户的登录状态
        updateLoginUserStatus(loginRequestDTO.getPhoneNumber(), FirstLoginStatusEnum.NO);

        //调用登录生产者向mq中发送消息
        sendFirstLoginMessage(loginRequestDTO);
    }

    @Override
    public void resetFirstLoginStatus(String phoneNumber) {
        updateLoginUserStatus(phoneNumber,FirstLoginStatusEnum.YES);
    }


    /**
     * 向rocketmq发送用户第一次登录的信息
     * @param loginRequestDTO
     */
    private void sendFirstLoginMessage(LoginRequestDTO loginRequestDTO) {
        //性能优化，异步发送消息到mq中
        Message message = new Message();
        message.setTopic(loginTopic);
        message.setBody(JSON.toJSONString(loginRequestDTO).getBytes(StandardCharsets.UTF_8));

        try {
            logger.info("开始推送消息----------");
            SendResult sendResult = loginMqProducer.send(message);
            logger.info("结束推送消息，返回结果：{}",sendResult);
        } catch (Exception e) {
            logger.info("推送信息到mq失败， 失败信息：{}", e);
        }
    }

    /**
     * 更改用户的标志状态
     *
     * @param phoneNumber
     */
    private void updateLoginUserStatus(String phoneNumber, FirstLoginStatusEnum loginStatusEnum) {
        //1查询当前用户登录的状态
        MysqlRequestDTO mysqlRequestDTO = new MysqlRequestDTO();
        mysqlRequestDTO.setSql("update t_member set first_login_status = ? where beid = 1563 and mobile = ?");
        mysqlRequestDTO.setPhoneNumber(phoneNumber);
        List<Object> params = new ArrayList<>();
        params.add(loginStatusEnum.getStatus());
        params.add(phoneNumber);

        mysqlRequestDTO.setParams(params);
        logger.info("更改状态前的参数 {}", JSON.toJSONString(mysqlRequestDTO));
        CommonResponse<Integer> update = mysqlApi.update(mysqlRequestDTO);
        logger.info("更改状态后的返回结果 {}", JSON.toJSONString(update));
    }



    /**
     * 判断用户是否是第一次登录
     *
     * @param loginRequestDTO
     * @return
     */
    private boolean wthrFirstLogin(LoginRequestDTO loginRequestDTO) {

        //1查询当前用户登录的状态
        MysqlRequestDTO mysqlRequestDTO = new MysqlRequestDTO();
        mysqlRequestDTO.setSql("select first_login_status from t_member where id = ? ");
        mysqlRequestDTO.setPhoneNumber(loginRequestDTO.getPhoneNumber());
        List<Object> params = new ArrayList<>();
        params.add(loginRequestDTO.getUserId());
        mysqlRequestDTO.setParams(params);
        mysqlRequestDTO.setProjectTypeEnum(LittleProjectTypeEnum.ROCKETMQ);

        logger.info("第一次登录的查询参数 {}", JSON.toJSONString(mysqlRequestDTO));
        CommonResponse<List<Map<String, Object>>> response = mysqlApi.query(mysqlRequestDTO);
        logger.info("第一次登录的查询返回结果集 {}", JSON.toJSONString(response));

        //判断用户的状态是否是第一次登录的状态
        if (Objects.equals(ErrorCodeEnum.SUCCESS, response.getCode()) &&
                !CollectionUtils.isEmpty(response.getData())) {
            Map<String, Object> responseMap = response.getData().get(0);
            int first_login_status = Integer.parseInt(
                    String.valueOf(responseMap.get("first_login_status")));
            return Objects.equals(first_login_status, FirstLoginStatusEnum.YES.getStatus());

        }
        return false;
    }
}
