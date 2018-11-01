package com.chlian.mqservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AutoConfigurationPackage
public class MqServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqServiceApplication.class, args);
    }
}
