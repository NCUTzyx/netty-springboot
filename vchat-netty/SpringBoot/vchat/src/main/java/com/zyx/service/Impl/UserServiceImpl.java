package com.zyx.service.Impl;

import com.zyx.enums.MsgActionEnum;
import com.zyx.enums.MsgIsSignEnum;
import com.zyx.enums.SearchFriendsStatusEnum;
import com.zyx.mapper.*;
import com.zyx.netty.entity.ChatMsg;
import com.zyx.netty.entity.DataContent;
import com.zyx.netty.entity.UserChannelRel;
import com.zyx.pojo.ChatMessage;
import com.zyx.pojo.Relevance;
import com.zyx.pojo.User;
import com.zyx.pojo.UserRequest;
import com.zyx.pojo.vo.FriendRequestVO;
import com.zyx.pojo.vo.MyFriendsVO;
import com.zyx.service.UserService;
import com.zyx.utils.FastDFSClient;
import com.zyx.utils.FileUtils;
import com.zyx.utils.JsonUtils;
import com.zyx.utils.QRCodeUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author 张宇森
 * @version 1.0
 * SUPPORTS 主要用于查询
 * REQUIRED 主要用于增删改
 */
// Service 没有命名默认为 userServiceImpl
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RelevanceMapper relevanceMapper;

    @Resource
    private UserRequestMapper userRequestMapper;

    @Resource
    private UserCustomMapper userCustomMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Resource
    private Sid sid;

    @Resource
    private QRCodeUtils qrCodeUtils;

    @Resource
    private FastDFSClient fastDFSClient;

    @Override
    public boolean queryUsernameIsExist(String username) {

        User userTemp = new User();
        userTemp.setUsername(username);
        User user = userMapper.selectOne(userTemp);
        return user != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User queryUserForLogin(String username, String password) {

        Example userExample = new Example(User.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);

        User user = userMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public User saveUser(User user) {

        //生成唯一id
        String id = sid.nextShort();
        user.setId(id);

        // 为每个用户生成唯一的二维码
        String content = "vchat_qrcode:[" + user.getUsername() +"]";
        String qrCodePath = "E:\\user" + id + "qrcode.png";
        qrCodeUtils.createQRCode(qrCodePath,content);

        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);
        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setQrcode(qrCodeUrl);

        user.setUsername(user.getUsername());
        user.setUserPhoto("");
        user.setUserPhotoBig("");
        user.setNickname(user.getUsername());
        user.setCid(user.getCid());

        userMapper.insert(user);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public User updateUserInfo(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer searchKey(String myId, String friendUsername) {

        //查询用户
        User user = queryUserByName(friendUsername);
        if(user == null){
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }

        if(user.getId().equals(myId)){
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }

        Example friend = new Example(Relevance.class);
        Criteria criteria = friend.createCriteria();
        criteria.andEqualTo("userId",myId);
        criteria.andEqualTo("friendId",user.getId());

        Relevance relevance = relevanceMapper.selectOneByExample(friend);
        if(relevance != null){
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User queryUserByName(String userName) {

        Example friend = new Example(User.class);
        Criteria criteria = friend.createCriteria();
        criteria.andEqualTo("username",userName);
        return userMapper.selectOneByExample(friend);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String myId, String friendUsername) {

        //根据微聊名查询好友的信息
        User friend = queryUserByName(friendUsername);

        //1. 查询发送好友请求表
        Example example = new Example(UserRequest.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId",myId);
        criteria.andEqualTo("acceptUserId",friend.getId());
        UserRequest request = userRequestMapper.selectOneByExample(example);
        if(request == null){
            //不是你的好友,且以往没有好友申请，则新增好友记录
            String requestId = sid.nextShort();
            UserRequest userRequest = new UserRequest();
            userRequest.setId(requestId);
            userRequest.setSendUserId(myId);
            userRequest.setAcceptUserId(friend.getId());
            userRequest.setSendTime(new Date());
            userRequestMapper.insert(userRequest);
        }else {
            //以往有过好友申请 更新时间
            UserRequest userRequest = new UserRequest();
            userRequest.setId(request.getId());
            userRequest.setSendTime(new Date());
            userRequestMapper.updateByPrimaryKeySelective(userRequest);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
        return userCustomMapper.queryFriendRequestList(acceptUserId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendsRequest(String sendUserId, String acceptUserId) {

        Example example = new Example(UserRequest.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId",sendUserId);
        criteria.andEqualTo("acceptUserId",acceptUserId);
        userRequestMapper.deleteByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void acceptFriendsRequest(String sendUserId, String acceptUserId) {

        Example example = new Example(Relevance.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",acceptUserId);
        criteria.andEqualTo("friendId",sendUserId);
        Relevance relevance = relevanceMapper.selectOneByExample(example);
        if(relevance == null){
            //1.正向添加信息
            saveFriends(sendUserId,acceptUserId);
            //2.反向添加好友信息
            saveFriends(acceptUserId,sendUserId);
            //3.删除好友申请信息
            deleteFriendsRequest(sendUserId,acceptUserId);

            Channel sendChannel = UserChannelRel.get(sendUserId);
            if(sendChannel != null) {

                // 使用WebSocket主动推送消息到请求发起者，更新他的通讯录为最新
                DataContent dataContent = new DataContent();
                dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

                sendChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {

        return userCustomMapper.queryMyFriendList(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveMsg(ChatMsg chatMsg) {

        ChatMessage chatMessage = new ChatMessage();
        String id = sid.nextShort();
        chatMessage.setId(id);
        chatMessage.setSendUser(chatMsg.getSenderId());
        chatMessage.setAcceptUser(chatMsg.getReceiverId());
        chatMessage.setMessage(chatMsg.getMessage());
        chatMessage.setIsRead(MsgIsSignEnum.unsign.getType());
        chatMessage.setCreateTime(new Date());

        chatMessageMapper.insert(chatMessage);
        //返回需要签收的消息记录id
        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        userCustomMapper.batchUpdateMsgSigned(msgIdList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ChatMessage> getUnReadMsgList(String acceptUserId) {

        Example example = new Example(ChatMessage.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("acceptUser",acceptUserId);
        criteria.andEqualTo("isRead",0);
        return chatMessageMapper.selectByExample(example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    protected User queryUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveFriends(String sendUserId, String acceptUserId){

        Relevance relevance = new Relevance();
        String id = sid.nextShort();
        relevance.setId(id);
        relevance.setUserId(acceptUserId);
        relevance.setFriendId(sendUserId);
        relevanceMapper.insert(relevance);



    }

}
