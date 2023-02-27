package com.zyx.pojo.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBO {

    private String userId;
    private String photoDate;
    private String nickname;

}