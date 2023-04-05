package com.openvelog.openvelogbe;

import com.openvelog.openvelogbe.blog.service.BlogService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class OpenVelogBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenVelogBeApplication.class, args);
    }

    // viewCountSum, wishCountSum 데이터 초기화를 위해 딱 한번 실행하고자 넣은 코드
    /*@Bean
    public CommandLineRunner initializeViewCountSumAndWishCountSum(BlogService blogService) {
        return (args) -> {
            blogService.initializeViewCountSumAndWishCountSum();
        };
    }*/
}
