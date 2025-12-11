package com.tikkl.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TikklBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(TikklBankApplication.class, args);
    }
}
