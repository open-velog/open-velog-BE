package com.openvelog.openvelogbe.openSearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.elasticsearch")
@ConfigurationPropertiesScan(basePackages = "com.openvelog.openvelogbe")
//@PropertySource("classpath:application-dev.properties")
public class OpenSearchProperties {
    private final Rest rest = new Rest();

    public Rest getRest() { return rest; }

    public static class Rest {
        private String uris;
        private String username;
        private String password;

        public String getUris() {
            return uris;
        }

        public void setUris(String uris) {
            this.uris = uris;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
