package com.example.coffeeservice.config;

import com.example.coffeeservice.service.CustomMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class JmsConfig  {

    public static final String BREWING_REQUEST_QUEUE = "brewing-request";
    public static final String NEW_INVENTORY_QUEUE = "new-inventory";
    public static final String VALIDATE_ORDER_QUEUE = "validate-order";
    public static final String VALIDATE_ORDER_RESULT_QUEUE = "validate-order-result";

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter customJmsMessageConverter(ObjectMapper objectMapper) {
        CustomMessageConverter converter = new CustomMessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
