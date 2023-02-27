package com.zyx.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author 张宇森
 * @version 1.0
 */
public class WSServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        ChannelPipeline pipeline = channel.pipeline();

        //websocket 基于 http 协议 需要 http 编解码器
        pipeline.addLast(new HttpServerCodec());

        //写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());

        //对HttpMessage 进行聚合，聚合成FullHttpRequest 或 FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));

        //===========================增加心跳机制=================================
        /**
         * 针对客户端，如果在 60秒没有向服务端发送读写心跳（ALL），则主动断开
         * 如果是读空闲(20秒)或者写空闲(40秒)，则不做处理
         * 用于激活空闲事件  Creates a new instance firing IdleStateEvents.
         */
        pipeline.addLast(new IdleStateHandler(20,40,60, TimeUnit.SECONDS));
        //自定义的空闲状态检测
        pipeline.addLast(new HeartbeatHandler());
        //===========================以上用于支持http协议==========================
        /**
         * websocket 服务器处理的协议， 用于指定给客户端连接访问的路由
         * 此handler 会帮我们处理一些复杂繁重的事情
         * 会处理握手动作 handshaking(close ping pong) ping + pong = 心跳
         * 对于websocket来讲，都是以 frames 进行传输的，不同数据类型对应的frames也不同
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //自定义的handler
        pipeline.addLast(new ChatHandler());
    }
}
