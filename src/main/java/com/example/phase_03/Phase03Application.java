package com.example.phase_03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Phase03Application {
    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(Phase03Application.class, args);
    }

}
