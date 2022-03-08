package com.example.orderservice.services;

import com.example.orderservice.model.events.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.io.StringWriter;

@Getter @Setter
public class CustomMessageConverter implements MessageConverter {

    private ObjectMapper objectMapper;
    private String typeIdPropertyName;

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        Message message = null;

        try {
            message = this.mapToTextMessage(object, session, this.objectMapper.writer());
            this.setTypeIdOnMessage(object, (Message)message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            String typeId = message.getStringProperty(this.typeIdPropertyName);
            TextMessage textMessage = (TextMessage) message;
            Object object = convertToObject(typeId, textMessage);

            return object;
            
        } catch (IOException var3) {
            throw new MessageConversionException("Failed to convert JSON message content", var3);
        }
    }

    public Object convertToObject(String typeId, TextMessage textMessage) throws JMSException, IOException {
        Object object = null;

        if (typeId.equals("AllocateCoffeeOrderRequest")) {
            object = this.objectMapper.readValue(textMessage.getText(), AllocateCoffeeOrderRequest.class);
        } else if (typeId.equals("AllocateCoffeeOrderResult")) {
            object = this.objectMapper.readValue(textMessage.getText(), AllocateCoffeeOrderResult.class);
        } else if (typeId.equals("AllocationFailureEvent")) {
            object = this.objectMapper.readValue(textMessage.getText(), AllocationFailureEvent.class);
        } else if (typeId.equals("CoffeeOrderValidationResult")) {
            object = this.objectMapper.readValue(textMessage.getText(), CoffeeOrderValidationResult.class);
        } else if (typeId.equals("DeAllocateOrderRequest")) {
            object = this.objectMapper.readValue(textMessage.getText(), DeAllocateOrderRequest.class);
        } else if (typeId.equals("ValidateCoffeeOrderRequest")) {
            object = this.objectMapper.readValue(textMessage.getText(), ValidateCoffeeOrderRequest.class);
        } else {
            throw new JMSException("Type is not recognized");
        }

        return object;
    }

    protected TextMessage mapToTextMessage(Object object, Session session, ObjectWriter objectWriter) throws JMSException, IOException {
        StringWriter writer = new StringWriter(1024);
        objectWriter.writeValue(writer, object);
        return session.createTextMessage(writer.toString());
    }


    protected void setTypeIdOnMessage(Object object, Message message) throws JMSException {
        if (this.typeIdPropertyName != null) {
            String typeId = object.getClass().getSimpleName();
            message.setStringProperty(this.typeIdPropertyName, typeId);
        }
    }
}
