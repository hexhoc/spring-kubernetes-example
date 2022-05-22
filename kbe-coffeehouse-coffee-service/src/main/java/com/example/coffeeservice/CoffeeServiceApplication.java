package com.example.coffeeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients //needs to be on SBA class
//@SpringBootApplication encapsulates @Configuration, @EnableAutoConfiguration, and @ComponentScan
@EnableKafka
@EnableScheduling
@SpringBootApplication
public class CoffeeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeServiceApplication.class, args);
    }

}
