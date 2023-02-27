package com.zyx.service;

import com.zyx.netty.entity.ChatMsg;
import com.zyx.pojo.ChatMessage;
import com.zyx.pojo.User;
import com.zyx.pojo.vo.FriendRequestVO;
import com.zyx.pojo.vo.MyFriendsVO;

import java.util.List;

/**
 * @author 张宇森
 * @version 1.0
 */
public interface UserService {

    //判断用户名是否存在
    boolean queryUsernameIsExist(String username);

    //验证登陆注册，并返回该对象
    User queryUserForLogin(String username, String password);

    //注册成功，存储user信息
    User saveUser(User user);

    //上传用户头像
    User updateUserInfo(User user);

    //添加好友状态信息
    Integer searchKey(String myId,String friendUsername);

    //根据用户名查询用户对象
    User queryUserByName(String userName);

    //添加好友请求
    void sendFriendRequest(String myId,String friendUsername);

    //好友申请列表
    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    //删除好友请求
    void deleteFriendsRequest(String sendUserId,String acceptUserId);

    //同意好友请求
    void acceptFriendsRequest(String sendUserId,String acceptUserId);

    //返回好友列表
    List<MyFriendsVO> queryMyFriends(String userId);

    //保存用户聊天消息,并返回签收消息的id
    String saveMsg(ChatMsg chatMsg);

    //批量签收消息
    void updateMsgSigned(List<String> msgIdList);

    //获取未签收的消息列表
    List<ChatMessage> getUnReadMsgList(String acceptUserId);

}
