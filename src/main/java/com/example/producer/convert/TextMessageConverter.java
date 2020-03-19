package com.example.producer.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author LXJ
 * @description
 * @date 2020/3/8 11:36
 */
public class TextMessageConverter implements MessageConverter {
    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        Message message = new Message(o.toString().getBytes(),messageProperties);
        return message;
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        String contentType = message.getMessageProperties().getContentType();
        if(null != contentType && contentType.contains("text")){
            System.err.println("消息转换");
            return new String(message.getBody());

        }
        return message.getBody();
    }
}
