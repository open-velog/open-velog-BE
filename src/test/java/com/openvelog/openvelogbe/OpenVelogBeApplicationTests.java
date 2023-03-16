package com.openvelog.openvelogbe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EnableJpaAuditing
@EnableCaching
@ActiveProfiles("test")
class OpenVelogBeApplicationTests {

    @Test
    void contextLoads() {
    }

}
