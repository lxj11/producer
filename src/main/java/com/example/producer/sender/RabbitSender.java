package com.example.producer.sender;

import com.alibaba.fastjson.JSONObject;
import com.example.producer.entity.Order;
import com.example.producer.utils.FastJsonConvertUtil;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author LXJ
 * @description
 * @date 2020/3/2 17:57
 */
@Component
public class RabbitSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //消息发送到交换机，调用该方法
    //1.如果消息没有到达exchange则ack=false,
    //2.如果消息到达exchagne,则ack=true
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.err.println("correlationData: "+correlationData);
            System.err.println("ack: "+ack);
            if(!ack){
                System.err.println("异常处理。。。。");
            }
        }
    };

    //当消息从交换机到队列失败时，该方法被调用。（若成功，则不调用）
    //需要注意的是：该方法调用后，confirmCallBack中的confirm方法也会被调用，且ack = true
    final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
                                    String exchange, String routingKey) {
            System.err.println("return exchange: "+exchange+",routingKey: "+routingKey+
                    ",replyCode"+replyCode+",replyText"+replyText);
        }
    };

    public void send(Object message,Map<String,Object> properties ) throws Exception{
        MessageHeaders mhs = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message,mhs);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        //需设置mandatory=true,否则不回调,消息就丢了
        rabbitTemplate.setMandatory(true);
        CorrelationData correlationData = new CorrelationData();
        //id全局唯一
        correlationData.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("exchange-1","springboot.hello",msg,correlationData);
    }

    public void sendOrder(Order order) throws Exception{
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        //需设置mandatory=true,否则不回调,消息就丢了
        rabbitTemplate.setMandatory(true);
        CorrelationData correlationData = new CorrelationData();
        //id全局唯一
        correlationData.setId(UUID.randomUUID().toString());
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSON(order).toString());
        rabbitTemplate.convertAndSend("exchange-2","springboot.hello",jsonObject,correlationData);
    }


}
