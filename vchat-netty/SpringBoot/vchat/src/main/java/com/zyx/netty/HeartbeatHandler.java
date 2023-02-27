package com.zyx.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author 张宇森
 * @version 1.0
 * 心跳机制 =》用于检测channel的心跳handler
 * 这里不需要实现channelRead0 方法 所以继承ChannelInboundHandlerAdapter即可
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    //A user event triggered by IdleStateHandler when a Channel is idle.
    //用户事件触发
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        //(用于触发用户事件，包含读空闲/写空闲/读写空闲)
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("进入读空闲...");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("进入写空闲...");
            } else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("进入读写空闲...");
                System.out.println("channel关闭前，总的channel的数量为:" + ChatHandler.users.size());
                Channel channel = ctx.channel();
                //关闭不需要的channel,防止资源浪费
                channel.close();
                System.out.println("channel关闭后，总的channel的数量为:" + ChatHandler.users.size());
            }
        }
    }
}

