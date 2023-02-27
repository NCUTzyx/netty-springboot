package com.zyx.controller;

import com.zyx.enums.OperatorFriendRequestTypeEnum;
import com.zyx.enums.SearchFriendsStatusEnum;
import com.zyx.pojo.ChatMessage;
import com.zyx.pojo.User;
import com.zyx.pojo.bo.UserBO;
import com.zyx.pojo.vo.MyFriendsVO;
import com.zyx.pojo.vo.UserVO;
import com.zyx.service.UserService;
import com.zyx.utils.FastDFSClient;
import com.zyx.utils.FileUtils;
import com.zyx.utils.JSONResult;
import com.zyx.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 张宇森
 * @version 1.0
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private FastDFSClient fastDFSClient;

    //登陆注册
    @PostMapping("/login")
    public JSONResult login(@RequestBody User user) throws Exception {

        System.out.println(user);

        // 1. 判断用户名和密码不能为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("用户名或密码不能为空...");
        }

        User result = null;
        // 2. 判断用户是否存在就登陆，如果不存在则注册。
        if (userService.queryUsernameIsExist(user.getUsername())) {
            // 登录
            result = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (result == null) {
                return JSONResult.errorMsg("用户名或密码不正确...");
            }

        } else {
            //注册
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            result = userService.saveUser(user);

        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(result,userVO);

        return JSONResult.ok(userVO);
    }

    //上传照片
    @PostMapping("/upload")
    public JSONResult upload(@RequestBody UserBO userBo) throws Exception {
        
        //获取前端传过来的base64字符串，转换为文件对象再进行上传
        String base64Data = userBo.getPhotoDate();
        String userPhotoPath = "E:\\user" + userBo.getUserId() + "userPhoto.png";
        FileUtils.base64ToFile(userPhotoPath,base64Data);

        //上传文件到FastDfs
        MultipartFile photoFile = FileUtils.fileToMultipart(userPhotoPath);
        String url = fastDFSClient.uploadBase64(photoFile);
        System.out.println(url);

        // "asdsdfgfnhgnmnhjmjkm.png"
        // "asdsdfgfnhgnmnhjmjkm_80x80.png"

        //获取缩略图的url
        String thump = "_80x80.";
        String[] split = url.split("\\.");
        //拼接url
        String thumpImageUrl = split[0] + thump + split[1];

        // 更新用户头像
        User user = new User();
        user.setId(userBo.getUserId());
        user.setUserPhoto(thumpImageUrl);
        user.setUserPhotoBig(url);

        User result = userService.updateUserInfo(user);

        return JSONResult.ok(result);
    }

    //更新用户昵称
    @PostMapping("/nickname")
    public JSONResult uploadNickname(@RequestBody UserBO userBo) {

        System.out.println(userBo);

        // 更新用户头像
        User user = new User();
        user.setId(userBo.getUserId());
        user.setNickname(userBo.getNickname());
        User result = userService.updateUserInfo(user);
        return JSONResult.ok(result);
    }

    //搜索好友 匹配查询
    @PostMapping("/search/{myId}/{friendUsername}")
    public JSONResult searchFriends(@PathVariable("myId")String myId, @PathVariable("friendUsername")String friendUsername) {

        //1.判断 myId 和 friendUsername 存在
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(friendUsername)) {
            return JSONResult.errorMsg("");
        }

        //2.搜索结果
        Integer status = userService.searchKey(myId,friendUsername);
        if(status.equals(SearchFriendsStatusEnum.SUCCESS.getStatus())) {

            User user = userService.queryUserByName(friendUsername);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user,userVO);
            return JSONResult.ok(userVO);

        } else {
            String msgByKey = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(msgByKey);
        }
    }

    //发送添加朋友请求
    @PostMapping("/add/{myId}/{friendUsername}")
    public JSONResult addFriends(@PathVariable("myId")String myId, @PathVariable("friendUsername")String friendUsername)  {

        //1.判断 myId 和 friendUsername 存在
        if (StringUtils.isBlank(myId) || StringUtils.isBlank(friendUsername)) {
            return JSONResult.errorMsg("");
        }

        //2.搜索结果
        Integer status = userService.searchKey(myId,friendUsername);
        if(status.equals(SearchFriendsStatusEnum.SUCCESS.getStatus())) {

            userService.sendFriendRequest(myId,friendUsername);

        } else {
            String msgByKey = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(msgByKey);
        }

        return JSONResult.ok();
    }

    //返回好友申请列表
    @PostMapping("/return/{acceptId}")
    public JSONResult returnFriends(@PathVariable("acceptId")String acceptId){

        //1.判断 acceptId 存在
        if (StringUtils.isBlank(acceptId)){
            return JSONResult.errorMsg("");
        }
        //返回用户接收到的好友申请
        return JSONResult.ok(userService.queryFriendRequestList(acceptId));
    }

    //同意或拒绝好友申请
    @PostMapping("/aor/{acceptUserId}/{sendUserId}/{status}")
    public JSONResult aorFriends(@PathVariable("acceptUserId")String acceptUserId,
                                 @PathVariable("sendUserId")String sendUserId,
                                 @PathVariable("status")Integer status){
        //1.判断是否为null
        if (StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId) || status == null ){
            return JSONResult.errorMsg("");
        }
        String msgByType = OperatorFriendRequestTypeEnum.getMsgByType(status);
        if (StringUtils.isBlank(msgByType)){
            return JSONResult.errorMsg("");
        }

        //2.判断操作类型
        if(status.equals(OperatorFriendRequestTypeEnum.REFUSE.type)){
            //拒绝，则直接删除数据库请求记录
            userService.deleteFriendsRequest(sendUserId,acceptUserId);
        } else if(status.equals(OperatorFriendRequestTypeEnum.ACCEPT.type)){
            //同意，则增加好友记录到数据库，再删除数据库请求记录
            userService.acceptFriendsRequest(sendUserId,acceptUserId);
        }

        //返回好友列表
        List<MyFriendsVO> myFriendsVOS = userService.queryMyFriends(acceptUserId);
        return JSONResult.ok(myFriendsVOS);
    }
    //返回用户通讯录列表
    @PostMapping("/book/{userId}")
    public JSONResult bookList(@PathVariable("userId")String userId){

        //1.判断是否为null
        if (StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("");
        }
        //返回好友列表
        List<MyFriendsVO> myFriendsVOS = userService.queryMyFriends(userId);
        return JSONResult.ok(myFriendsVOS);
    }

    //用户获取未签收的消息列表
    @PostMapping("/getUnReadMsgList/{acceptUserId}")
    public JSONResult getUnReadMsgList(@PathVariable("acceptUserId")String acceptUserId){

        //1.判断 acceptUserId 存在
        if (StringUtils.isBlank(acceptUserId)){
            return JSONResult.errorMsg("");
        }
        //查询列表
        List<ChatMessage> unReadMsgList = userService.getUnReadMsgList(acceptUserId);
        return JSONResult.ok(unReadMsgList);
    }
}
