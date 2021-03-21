package com.yunchuan.bilibili;



import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;




@MapperScan("com.yunchuan.bilibili.dao")
@EnableTransactionManagement
@SpringBootApplication
public class BilibiliApplication {

    public static void main(String[] args) {
        SpringApplication.run(BilibiliApplication.class, args);}
}
