package com.codesoftlabs.umbral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.codesoftlabs.umbral.repository")
public class UmbralApplication {

    public static void main(String[] args) {
        SpringApplication.run(UmbralApplication.class, args);
    }

}
