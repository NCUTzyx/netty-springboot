package com.zyx.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * @author 张宇森
 * @version 1.0
 */

@Component
public class WSServer {

    private static class SingleWSServer{
        static final WSServer instance = new WSServer();
    }

    public static WSServer getInstance() {
        return SingleWSServer.instance;
    }

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;
    private final ServerBootstrap server;
    private ChannelFuture channelFuture;

    public WSServer() {

        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WSServerInitializer());
    }

    public void start(){
        this.channelFuture = server.bind(8088);
        System.err.println("netty websocket server 启动完毕...");
    }
}
