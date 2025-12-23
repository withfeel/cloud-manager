package com.cloudmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.cloudmanager.mapper")
public class CloudManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudManagerApplication.class, args);
    }
}