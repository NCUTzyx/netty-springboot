package com.zyx;

import com.zyx.netty.WSServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author 张宇森
 * @version 1.0
 * Netty server 启动程序 => 监听springboot容器，再启动server
 */
@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() == null) {  //应用程序上下文是否为根上下文
            try {
                WSServer.getInstance().start(); //启动netty
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
