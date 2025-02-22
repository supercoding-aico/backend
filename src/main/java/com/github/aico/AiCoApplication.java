package com.github.aico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AiCoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCoApplication.class, args);
    }

}
