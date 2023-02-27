package com.zyx.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private String id;
    private String username;
    private String userPhoto;
    private String userPhotoBig;
    private String nickname;
    private String qrcode;

}