package com.zyx.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张宇森
 * @version 1.0
 * 发送好友请求的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestVO{

    private String sendUserId;
    private String sendUsername;
    private String sendUserPhoto;
    private String sendNickname;

}
