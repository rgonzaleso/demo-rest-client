package com.rgonzaleso.restclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoRestClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoRestClientApplication.class, args);
    }

}
