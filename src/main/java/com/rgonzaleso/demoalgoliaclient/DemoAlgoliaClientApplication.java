package com.rgonzaleso.demoalgoliaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoAlgoliaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoAlgoliaClientApplication.class, args);
    }

}
