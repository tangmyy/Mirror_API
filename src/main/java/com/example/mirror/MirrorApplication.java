// src/main/java/com/example/mirror/MirrorApplication.java
package com.example.mirror;

import com.example.mirror.service.Impl.BigModelServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class MirrorApplication implements CommandLineRunner {

    @Autowired
    private BigModelServiceImpl bigModelServiceImpl;

    public static void main(String[] args) {
        SpringApplication.run(MirrorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        bigModelServiceImpl.start();
    }
}


