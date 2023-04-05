package com.openvelog.openvelogbe.common.config;

import com.openvelog.openvelogbe.common.entity.SearchLog;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties properties;

    @Bean
    public ConsumerFactory<String, SearchLog> consumerFactory() {
        JsonDeserializer<SearchLog> deserializer = new JsonDeserializer<>(SearchLog.class);
        deserializer.setUseTypeMapperForKey(true);
        return new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SearchLog> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SearchLog> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setSyncCommits(true);
        return factory;
    }

    @Bean
    public ProducerFactory<String, SearchLog> producerFactory() {
        JsonSerializer<SearchLog> serializer = new JsonSerializer<>();
        return new DefaultKafkaProducerFactory<>(properties.buildProducerProperties(), new StringSerializer(), serializer);
    }


    @Bean
    public KafkaTemplate<String, SearchLog> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
