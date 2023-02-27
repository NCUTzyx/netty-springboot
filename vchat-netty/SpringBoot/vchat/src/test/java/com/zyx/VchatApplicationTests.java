package com.zyx;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;

@SpringBootTest
class VchatApplicationTests {

    @Test
    void contextLoads() throws Exception {

    InetAddress localHost = InetAddress.getLocalHost();
    System.out.println("本地项目地址:  " + localHost.getHostAddress());
    }

}
