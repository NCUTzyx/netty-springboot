package com.zyx.mapper;

import com.zyx.pojo.User;
import com.zyx.pojo.vo.FriendRequestVO;
import com.zyx.pojo.vo.MyFriendsVO;
import com.zyx.utils.MyMapper;

import java.util.List;

/**
 * @author 张宇森
 * @version 1.0
 */
public interface UserCustomMapper extends MyMapper<User>{

    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    List<MyFriendsVO> queryMyFriendList(String userId);

    void batchUpdateMsgSigned(List<String> msgIdList);
}
