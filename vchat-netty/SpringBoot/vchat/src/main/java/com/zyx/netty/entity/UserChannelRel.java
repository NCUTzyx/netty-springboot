package com.zyx.netty.entity;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张宇森
 * @version 1.0
 *  用户id 和 channel 的关联关系
 */
public class UserChannelRel {

    private static final HashMap<String, Channel> association = new HashMap<>();

    public static void put(String sendId,Channel channel){
        association.put(sendId,channel);
    }

    public static Channel get(String sendId){
        return association.get(sendId);
    }

    public static void output(){
        for(HashMap.Entry<String, Channel> en: association.entrySet()){
            System.out.println("UserId:" + en.getKey() + " ChannelId:" + en.getValue().id().asLongText());
        }
    }
}
