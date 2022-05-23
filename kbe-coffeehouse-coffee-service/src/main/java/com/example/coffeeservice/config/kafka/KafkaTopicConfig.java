package com.example.coffeeservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String BREWING_REQUEST_QUEUE = "brewing-request";
    public static final String NEW_INVENTORY_QUEUE = "new-inventory";
    public static final String VALIDATE_ORDER_QUEUE = "validate-order";
    public static final String VALIDATE_ORDER_RESULT_QUEUE = "validate-order-result";

    @Bean
    public NewTopic topicBrewingRequest() {
        return TopicBuilder.name(BREWING_REQUEST_QUEUE).build();
    }

    @Bean
    public NewTopic topicNewInventory() {
        return TopicBuilder.name(NEW_INVENTORY_QUEUE).build();
    }

    @Bean
    public NewTopic topicValidateOrder() {
        return TopicBuilder.name(VALIDATE_ORDER_QUEUE).build();
    }

    @Bean
    public NewTopic topicValidateOrderResult() {
        return TopicBuilder.name(VALIDATE_ORDER_RESULT_QUEUE).build();
    }


}