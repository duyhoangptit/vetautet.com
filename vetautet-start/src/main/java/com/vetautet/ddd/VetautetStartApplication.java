package com.vetautet.ddd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootApplication
public class VetautetStartApplication implements CommandLineRunner {

    @Value("${spring.application.name:unknown-app}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(VetautetStartApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("appName: {}", appName);
    }
}
