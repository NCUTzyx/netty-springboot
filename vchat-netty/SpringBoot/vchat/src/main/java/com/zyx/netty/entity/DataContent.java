package com.zyx.netty.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 张宇森
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataContent implements Serializable{

    private static final long serialVersionUID = 2992822075968788324L;
    private Integer action;  //动作类型
    private ChatMsg chatMsg; // 用户聊天信息entity
    private String extend;   //扩展字段

}
