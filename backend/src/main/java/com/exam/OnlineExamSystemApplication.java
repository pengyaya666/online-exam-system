package com.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.exam.mapper")
public class OnlineExamSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineExamSystemApplication.class, args);
        System.out.println("========================================");
        System.out.println("  在线考试系统后端服务启动成功！");
        System.out.println("  接口地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}
