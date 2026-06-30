package com.kemiel.greenenergy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GreenEnergyPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreenEnergyPlatformApplication.class, args);
    }
}

