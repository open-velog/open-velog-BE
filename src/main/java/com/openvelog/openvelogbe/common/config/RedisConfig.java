package com.openvelog.openvelogbe.common.config;



import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.util.List;
import java.util.Map;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {
    @Value("${redis.record.view.count.lock.name}")
    private String viewCountLock;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.password}")
    public String password;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setPassword(password);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    @Qualifier("viewCountLock")
    public RedisLockRegistry redisViewCountLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, viewCountLock);
    }

//    @Bean
//    RedisMessageListenerContainer keyExpirationListenerContainer() {
//
//        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
//        listenerContainer.setConnectionFactory(redisConnectionFactory());
//
//        listenerContainer.addMessageListener((message, pattern) -> {
//
//            String expiredKey = message.toString();

//            if (Boolean.FALSE.equals(redisTemplate().hasKey(expiredKey))) {
//                throw new RuntimeException("Key does not exist in Redis");
//            }
//            if (redisTemplate().type(expiredKey) != DataType.HASH) {
//                throw new RuntimeException("Key is not a Hash type");
//            }

//            List<String> expiredValue = redisTemplate().<String, String>opsForHash().values(expiredKey);
//
//            System.out.println(expiredKey);
//            System.out.println(expiredValue);
//            // event handling comes here
//            redisTemplate().opsForZSet().incrementScore("keywords", "send", 1);
//
//        }, new PatternTopic("__keyevent@*__:expired"));
//
//        return listenerContainer;
//    }
}
