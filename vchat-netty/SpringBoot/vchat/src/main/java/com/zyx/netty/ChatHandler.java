package com.zyx.netty;

import com.zyx.SpringUtil;
import com.zyx.enums.MsgActionEnum;
import com.zyx.netty.entity.ChatMsg;
import com.zyx.netty.entity.DataContent;
import com.zyx.netty.entity.UserChannelRel;
import com.zyx.service.UserService;
import com.zyx.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张宇森
 * @version 1.0
 * 处理消息的Handler
 * TextWebSocketFrame 在netty中，用于对websocket 专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    //用于记录和管理客户端所有的channel
    public static final ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        //1.获取客户端传输过来的消息
        String content = msg.text();
        //将字符串转化为实体对象
        DataContent dataContent = JsonUtils.jsonToPojo(content,DataContent.class);
        //获得动作类型
        Integer action = dataContent.getAction();

        //获取当前的channel
        Channel currentChannel = ctx.channel();

        //2.判断消息的类型，根据不同的类型来处理不同的业务
        if (action.equals(MsgActionEnum.CONNECT.type)){
            // 当websocket 第一次open的时候初始化 channel, 把用户的channel 和 用户的id关联起来
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelRel.put(senderId,currentChannel);

            //测试
            for(Channel c : users) {
                System.out.println(c.id().asLongText());
            }

            UserChannelRel.output();

        } else if (action.equals(MsgActionEnum.CHAT.type)){
            // 聊天类型的消息，把聊天记录保存到数据库（可加密），标记消息的签收状态[标记签收]
            ChatMsg chatMsg = dataContent.getChatMsg();
            String receiverId = chatMsg.getReceiverId();

            //保存消息到数据库，并且标记为 未读
            //因为这里无法自动注入service,所以需要引入SpringUtils
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            //发送消息
            // 从全局用户channel 中获取接收方的 channel
            Channel receiveChannel = UserChannelRel.get(receiverId);
            if(receiveChannel == null) {
                //TODO channel为空，代表用户已离线，推送消息（JPush,个推，小米推送）
            } else {
                // 当receiveChannel不为空时，从ChannelGroup中查找对应的channel是否存在
                Channel findChannel = users.find(receiveChannel.id());
                if (findChannel != null) {
                    //用户在线 => 发送消息
                    receiveChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg)));
                } else {
                    //用户离线 TODO 推送消息
                }
            }

        } else if (action.equals(MsgActionEnum.SIGNED.type)){

            // 签收消息类型，针对具体的消息进行签收，修改状态[标记已签收]
            UserService userService = (UserService) SpringUtil.getBean("userServiceImpl");
            //扩展字段在signed类型的消息中，代表需要去签收的消息id , -间隔
            String IDString = dataContent.getExtend();
            //获取所有需要签收消息的id
            String[] msgIds = IDString.split("-");

            List<String> msgIdList = new ArrayList<>();
            for (String msgId: msgIds) {
                //判断是否为null
                if (StringUtils.isNotBlank(msgId)) {
                    msgIdList.add(msgId);
                }
            }
            System.out.println(msgIdList.toString());

            if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
                // 批量签收消息
                userService.updateMsgSigned(msgIdList);
            }

        } else if (action.equals(MsgActionEnum.KEEPALIVE.type)){
            // 心跳类型的消息
            System.out.println("收到来自channel[" + currentChannel +"]的心跳包....");
        }
    }

    /**
     * 当客户端连接到服务端之后（打开连接）
     * 获取客户端的channel,并且存放到 ChannelGroup 中进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)  {
        //当客户端与服务端建立连接后，加入channel
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {

        String channelId = ctx.channel().id().asShortText();
        System.out.println("客户端"+channelId+"被移除");
        //当触发handlerRemoved， ChannelGroup会自动移除对应客户端的channel
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //打印错误信息
        cause.printStackTrace();
        //发生错误，则移除对应客户端的channel
        users.remove(ctx.channel());
    }
}
