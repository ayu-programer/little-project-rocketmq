package com.hm.little.project.rocketmq.api.common.constant;

/**
 * Redis操作的key
 */
public class RedisKeyConstant {

    /**
     * 第一次重复登录，保证幂等的key前缀
     */
    public static final String FIRST_LOGIN_DUPLICATION_KEY_PREFIX = "little:project:firstLoginDuplication:";


    /**
     * 酒店房间key的前缀
     */
    public static final String HOTEL_ROOM_KEY_PREFIX = "little:project:hotel:";
}
