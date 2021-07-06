package com.hm.little.project.rocketmq.api.hotel.service.impl;

import com.alibaba.fastjson.JSON;
import com.hm.little.project.rocketmq.api.hotel.dto.HotelRoom;
import com.hm.little.project.rocketmq.api.hotel.dto.RoomPicture;
import com.hm.little.project.rocketmq.api.hotel.enums.HotelBusinessErrorCodeEnum;
import com.hm.little.project.rocketmq.api.hotel.service.HotelRoomService;
import com.ruyuan.little.project.common.dto.CommonResponse;
import com.ruyuan.little.project.common.enums.ErrorCodeEnum;
import com.ruyuan.little.project.common.enums.LittleProjectTypeEnum;
import com.ruyuan.little.project.mysql.api.MysqlApi;
import com.ruyuan.little.project.mysql.dto.MysqlRequestDTO;
import com.ruyuan.little.project.redis.api.RedisApi;
import com.ruyuan.little.project.rocketmq.api.hotel.dto.HotelRoom;
import com.ruyuan.little.project.rocketmq.api.hotel.dto.RoomDescription;
import com.ruyuan.little.project.rocketmq.api.hotel.dto.RoomPicture;
import com.ruyuan.little.project.rocketmq.api.hotel.enums.HotelBusinessErrorCodeEnum;
import com.ruyuan.little.project.rocketmq.api.hotel.service.HotelRoomService;
import com.ruyuan.little.project.rocketmq.common.constant.RedisKeyConstant;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HotelRoomServiceImpl implements HotelRoomService {

    private Logger logger = LoggerFactory.getLogger(HotelRoomServiceImpl.class);
    @Autowired
    private HotelRoomCacheManager hotelRoomCacheManager;

    @Reference(version = "1.0.0",
            interfaceClass = RedisApi.class,
            cluster = "failfast")
    private RedisApi redisApi;


    /**
     * mysql dubbo api接口
     */
    @com.alibaba.dubbo.config.annotation.Reference(version = "1.0.0",
            interfaceClass = MysqlApi.class,
            cluster = "failfast")
    private MysqlApi mysqlApi;

    @Override
    public HotelRoom getRoomById(Long id, String phoneNumber) {

        //首先从jvm缓存里面取
        HotelRoom hotelRoom = hotelRoomCacheManager.getHotelRoomFromLocalCache(id);
        if (!Objects.isNull(hotelRoom)){
            logger.info("jvm缓存不为空，获取酒店房间信息 {}", JSON.toJSONString(hotelRoom));
            return hotelRoom;
        }

        logger.info("jvm缓存为空，获取酒店房间id {}", id);
        //如果缓存里面没有再从redis里面取，
        CommonResponse<String> response =
                                redisApi.get(RedisKeyConstant.HOTEL_ROOM_KEY_PREFIX,
                                phoneNumber,
                                LittleProjectTypeEnum.ROCKETMQ);
        //判断response结果
        if (Objects.equals(response.getCode(), ErrorCodeEnum.SUCCESS) &&
                !Objects.isNull(response.getData())){
            String data = response.getData();

            //判断字符长度
            if (StringUtils.hasLength(data)){
                logger.info("redis缓存不为空，获取酒店房间信息 {}",data);
                return JSON.parseObject(data,HotelRoom.class);
            }
        }
        //如果redis里面也没有就从数据库取
        logger.info("redis缓存为空，获取酒店房间id {}",id);
        return getHotelRoomById(id,phoneNumber);
    }

    /**
     * 根据房间id查询房间内容
     * @param id
     * @param phoneNumber
     * @return
     */
    private HotelRoom getHotelRoomById(Long id, String phoneNumber) {
        //1 先定义mysql的requestDTO
        MysqlRequestDTO mysqlRequestDTO = new MysqlRequestDTO();

        //2设置属性值
        mysqlRequestDTO.setPhoneNumber(phoneNumber);
        mysqlRequestDTO.setProjectTypeEnum(LittleProjectTypeEnum.ROCKETMQ);
        List<Object> paramlist = new ArrayList<>();
        paramlist.add(id);
        mysqlRequestDTO.setParams(Collections.singletonList(id));

        mysqlRequestDTO.setSql("select  "+ "id,"
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

        //执行mysqlapi
        CommonResponse<List<Map<String, Object>>> commonResponse =
                                            mysqlApi.query(mysqlRequestDTO);
        logger.info("获取到返回结果信息 {},参数信息",JSON.toJSONString(commonResponse),JSON.toJSONString(mysqlRequestDTO));

        //判断获取的结果
        if (Objects.equals(commonResponse.getCode(),ErrorCodeEnum.SUCCESS)){
            List<Map<String, Object>> mapList = commonResponse.getData();
            List<HotelRoom> hotelRoomList = mapList.stream().map(map ->{
                HotelRoom detailDTO = new HotelRoom();
                detailDTO.setId((Long) map.get("id"));
                detailDTO.setTitle(String.valueOf(map.get("title")));
                detailDTO.setPcate((Long) map.get("pcate"));
                detailDTO.setThumbUrl(String.valueOf(map.get("thumb_url")));
                detailDTO.setRoomDescription(JSON.parseObject(String.valueOf(map.get("description")), RoomDescription.class));
                String goods_banner = String.valueOf(map.get("goods_banner"));
                List<RoomPicture> roomPictures = JSON.parseArray(goods_banner, RoomPicture.class);
                detailDTO.setGoods_banner(roomPictures);
                detailDTO.setMarketprice((BigDecimal) map.get("marketprice"));
                detailDTO.setProductprice((BigDecimal) map.get("productprice"));
                detailDTO.setTotal((Integer) map.get("total"));
                detailDTO.setCreatetime((Long) map.get("createtime"));
                return detailDTO;
            }).collect(Collectors.toList());


            HotelRoom hotelRoom = hotelRoomList.get(0);
            //存到redis里面
            redisApi.set(RedisKeyConstant.HOTEL_ROOM_KEY_PREFIX+id,
                    JSON.toJSONString(hotelRoom),
                    phoneNumber,
                    LittleProjectTypeEnum.ROCKETMQ);

            //更新本地缓存
            hotelRoomCacheManager.updateLocalCache(hotelRoom);
            return hotelRoom;
        }
        logger.info("hotelRoomId {} data not exist",id);

        throw new RuntimeException(HotelBusinessErrorCodeEnum.HOTEL_ROOM_NOT_EXIST.getMessage());
    }
}
