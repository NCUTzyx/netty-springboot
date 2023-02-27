package com.zyx.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张宇森
 * @version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyFriendsVO {

    private String friendUserId;
    private String friendUsername;
    private String friendUserPhoto;
    private String friendNickname;
}
