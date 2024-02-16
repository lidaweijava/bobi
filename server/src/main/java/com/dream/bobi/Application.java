package com.dream.bobi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = {"com.dream.bobi"})
@Configuration
@MapperScan(basePackages = {"com.dream.bobi.commons.mapper"})
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
