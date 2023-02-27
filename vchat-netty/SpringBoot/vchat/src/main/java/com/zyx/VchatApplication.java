package com.zyx;

import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.zyx.mapper")
@ComponentScan(basePackages= {"com.zyx","org.n3r.idworker"})
public class VchatApplication {

    //注册SpringUtil 类
    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(VchatApplication.class, args);
    }

}
