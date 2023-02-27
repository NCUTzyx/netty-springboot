package com.zyx.netty.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 张宇森
 * @version 1.0
 *  聊天模型对象
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsg implements Serializable {

    private static final long serialVersionUID = 471168370662493608L;
    private String senderId;   //发送者的用户id
    private String receiverId;  //接受者的用户id
    private String message;   //聊天内容
    private String msgId;    //消息记录id => 需要签收的消息 id
}
