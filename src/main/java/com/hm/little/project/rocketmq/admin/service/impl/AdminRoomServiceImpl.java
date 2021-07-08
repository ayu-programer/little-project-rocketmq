package com.hm.little.project.rocketmq.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.common.enums.ErrorCodeEnum;
import com.ruyuan.little.project.common.enums.LittleProjectTypeEnum;
import com.ruyuan.little.project.mysql.api.MysqlApi;
import com.ruyuan.little.project.mysql.dto.MysqlRequestDTO;
import com.ruyuan.little.project.redis.api.RedisApi;
import com.ruyuan.little.project.rocketmq.admin.dto.AdminHotelRoom;
import com.ruyuan.little.project.rocketmq.admin.dto.AdminRoomDescription;
import com.ruyuan.little.project.rocketmq.admin.dto.AdminRoomPicture;
import com.ruyuan.little.project.rocketmq.admin.service.AdminRoomService;
import com.ruyuan.little.project.rocketmq.common.constant.RedisKeyConstant;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminRoomServiceImpl implements AdminRoomService {

    private Logger logger = LoggerFactory.getLogger(AdminRoomServiceImpl.class);


    @Reference(version = "1.0.0",interfaceClass = RedisApi.class,cluster = "failfast")
    private RedisApi redisApi;



    @Qualifier(value = "hotelRoomProducer")
    private DefaultMQProducer defaultMQProducer;
    /**
     * mysql dubbo服务
     */
    @Reference(version = "1.0.0",
            interfaceClass = MysqlApi.class,
            cluster = "failfast")
    private MysqlApi mysqlApi;


    @Value("${rocketmq.hotelRoom.topic}")
    private String hotelRoomTopic;

    @Override
    public CommonResponse add(AdminHotelRoom adminHotelRoom) {
        //房间数据mysql已经存储 这里只是请求转发过来写redis

        redisApi.set(RedisKeyConstant.HOTEL_ROOM_KEY_PREFIX+adminHotelRoom.getId(),
                JSON.toJSONString(adminHotelRoom),
                adminHotelRoom.getPhoneNumber(),
                LittleProjectTypeEnum.ROCKETMQ);
        return CommonResponse.success();
    }

    @Override
    public CommonResponse update(AdminHotelRoom adminHotelRoom) {

        String phoneNumber = adminHotelRoom.getPhoneNumber();

        //先定义mysql操作实体vo
        MysqlRequestDTO mysqlRequestDTO = new MysqlRequestDTO();
        mysqlRequestDTO.setPhoneNumber(phoneNumber);
        mysqlRequestDTO.setProjectTypeEnum(LittleProjectTypeEnum.ROCKETMQ);
        mysqlRequestDTO.setSql("UPDATE t_shop_goods SET pcate = ?, title = ?, " +
                "thumb_url = ?, productprice = ?, total = ?, totalcnf = ? WHERE id = ?");

        this.buildSqlParam(adminHotelRoom,mysqlRequestDTO);
        logger.info("执行sql操作之前，参数为 {}",JSON.toJSONString(mysqlRequestDTO));
        //更新数据库
        CommonResponse<Integer> response = mysqlApi.update(mysqlRequestDTO);
        logger.info("执行sql操作之前，结果为 {}",JSON.toJSONString(response));

        //更新完数据库后再更新redis
        if (Objects.equals(response.getCode(), ErrorCodeEnum.SUCCESS)){
            //1 需要再去数据库里面查一下，避免有些字段放到redis是空
            AdminHotelRoom hotelRoom = queryHotelRoom(adminHotelRoom);
            //2 保存数据到redis中
            redisApi.set(RedisKeyConstant.HOTEL_ROOM_KEY_PREFIX+adminHotelRoom.getId(),
                    JSON.toJSONString(hotelRoom),
                    phoneNumber,
                    LittleProjectTypeEnum.ROCKETMQ);

            //3 再推送消息到mq中
//            sendHotelRoomMessage(hotelRoom);
            sendHotelRoomMessage(hotelRoom.getId(),hotelRoom.getPhoneNumber());
        }
        return CommonResponse.success();
    }

    private void sendHotelRoomMessage(Long id, String phoneNumber) {
        AdminHotelRoom hotelRoom = new AdminHotelRoom();
        hotelRoom.setId(id);
        hotelRoom.setPhoneNumber(phoneNumber);
        Message message = new Message();
        message.setTopic(hotelRoomTopic);
        message.setBody(JSON.toJSONString(hotelRoom).getBytes(StandardCharsets.UTF_8));

        try {
            logger.info("开始推送消息到mq 参数 {}", JSON.toJSONString(hotelRoom.getId()));
            defaultMQProducer.send(message);
            logger.info("结束推送消息到mq 参数 {}", JSON.toJSONString(hotelRoom));
        } catch (Exception e) {
            logger.error("推送消息到mq失败",e);
        }
    }

    /**
     * 发送消息到mq
     * @param hotelRoom
     */
/*    private void sendHotelRoomMessage(AdminHotelRoom hotelRoom) {
        //异步发送消息到mq中
        Message message = new Message();
        message.setTopic(hotelRoomTopic);
        message.setBody(JSON.toJSONString(hotelRoom).getBytes(StandardCharsets.UTF_8));

        try {
            logger.info("开始推送消息到mq 参数 {}", JSON.toJSONString(hotelRoom.getId()));
            defaultMQProducer.send(message);
            logger.info("结束推送消息到mq 参数 {}", JSON.toJSONString(hotelRoom));
        } catch (Exception e) {
            logger.error("推送消息到mq失败",e);
        }

    }*/

    /**
     * 根据id查询酒店房间信息
     * @param adminHotelRoom
     * @return
     */
    private AdminHotelRoom queryHotelRoom(AdminHotelRoom adminHotelRoom) {
        //先定义mysql操作实体vo
        MysqlRequestDTO mysqlRequestDTO = new MysqlRequestDTO();
        mysqlRequestDTO.setPhoneNumber(adminHotelRoom.getPhoneNumber());
        mysqlRequestDTO.setProjectTypeEnum(LittleProjectTypeEnum.ROCKETMQ);
        mysqlRequestDTO.setParams(Collections.singletonList(adminHotelRoom.getId()));
        mysqlRequestDTO.setSql("SELECT "
                + "id,"
                + "title, "
                + "pcate, "
                + "thumb_url, "
                + "description, "
                + "goods_banner, "
                + "marketprice, "
                + "productprice, "
                + "total,"
                + "createtime "
                + "FROM "
                + "t_shop_goods "
                + "WHERE "
                + "id = ?");

        logger.info("执行sql操作之前，参数为 {}",JSON.toJSONString(mysqlRequestDTO));
        CommonResponse<List<Map<String, Object>>> response = mysqlApi.query(mysqlRequestDTO);

        logger.info("执行sql操作之后，结果集为 {}",JSON.toJSONString(response));


        if (Objects.equals(response.getCode(),ErrorCodeEnum.SUCCESS)){
            List<Map<String, Object>> mapList = response.getData();
            List<AdminHotelRoom> hotelRoomList = mapList.stream().map(map -> {
                AdminHotelRoom hotelRoom = new AdminHotelRoom();
                hotelRoom.setId((Long) map.get("id"));
                hotelRoom.setThumbUrl(String.valueOf(map.get("thumb_url")));
                hotelRoom.setTitle(String.valueOf(map.get("title")));
                hotelRoom.setPcate((Long) map.get("pcate"));
                hotelRoom.setRoomDescription(JSON.parseObject(String.valueOf(map.get("description")), AdminRoomDescription.class));
                String goods_banner = String.valueOf(map.get("goods_banner"));
                List<AdminRoomPicture> adminRoomPictures = JSON.parseArray(goods_banner, AdminRoomPicture.class);
                hotelRoom.setGoods_banner(adminRoomPictures);
                hotelRoom.setMarketprice((BigDecimal) map.get("marketprice"));
                hotelRoom.setProductprice((BigDecimal) map.get("productprice"));
                hotelRoom.setTotal((Integer) map.get("total"));
                hotelRoom.setCreatetime((Long) map.get("createtime"));
                return hotelRoom;
            }).collect(Collectors.toList());

            return hotelRoomList.get(0);
        }
        return null;
    }

    private void buildSqlParam(AdminHotelRoom adminHotelRoom, MysqlRequestDTO mysqlRequestDTO) {
        List<Object> paramList = new ArrayList<>();
        paramList.add(adminHotelRoom.getPcate());
        paramList.add(adminHotelRoom.getTitle());
        paramList.add(adminHotelRoom.getThumbUrl());
        paramList.add(adminHotelRoom.getProductprice());
        paramList.add(adminHotelRoom.getTotal());
        paramList.add(adminHotelRoom.getTotalcnf());
        paramList.add(adminHotelRoom.getId());
        mysqlRequestDTO.setParams(paramList);
    }
}
