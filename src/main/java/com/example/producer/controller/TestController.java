package com.example.producer.controller;

import com.example.producer.constant.RabbitMqConstant;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LXJ
 * @description
 * @date 2020/3/6 18:57
 */
@RestController
@Slf4j
@RequestMapping(value = "/test")
public class TestController {
    @Autowired
    private RabbitMqConstant rabbitMqConstant;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    private String exchange = "mq-exchange";
    private String queue = "mq-queue";
    private String routingkey = "test.#";

    @GetMapping(value = "/test")
    public void test(){
        String addresses = rabbitMqConstant.getAddresses();
        String password = rabbitMqConstant.getPassword();
        String username = rabbitMqConstant.getUsername();
        String virtualHost = rabbitMqConstant.getVirtualHost();
        System.out.println(virtualHost+"  "+ addresses+"  "+username+"  "+password);
    }

    @GetMapping(value = "mq")
    public void rabbitmq(){
        rabbitAdmin.declareExchange(new DirectExchange("test.direct",false,false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic",false,false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout",false,false));

    }

}
