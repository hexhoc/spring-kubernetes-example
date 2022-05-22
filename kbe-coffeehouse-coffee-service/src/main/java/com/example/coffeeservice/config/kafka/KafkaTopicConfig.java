package com.example.coffeeservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
class KafkaTopicConfig {

    @Bean
    public NewTopic topicBrewingRequest() {
        return TopicBuilder.name("brewing-request").build();
    }

    @Bean
    public NewTopic topicNewInventory() {
        return TopicBuilder.name("new-inventory").build();
    }

}