package com.it;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

/**
 * @author xwzStart
 * @create 2022-03-01 16:46
 */

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching //开启缓存注解功能
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功...");

        System.out.println(LocalDateTime.now() + "++++");
    }
}