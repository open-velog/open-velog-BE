package com.openvelog.openvelogbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OpenVelogBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenVelogBeApplication.class, args);
    }

}
