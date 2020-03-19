package com.example.producer;

import com.example.producer.entity.Order;
import com.example.producer.sender.RabbitSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LXJ
 * @description
 * @date 2020/3/8 19:43
 */
@SpringBootTest
public class RabbitMqTests {
    @Test
    void contextLoads() {
    }

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSender1() throws Exception{
        Map<String,Object> properties = new HashMap<>();
        properties.put("number","12345");
        properties.put("send_time",new Date());
        rabbitSender.send("Hello RabbitMq For Spring Boot",properties);
    }
    @Test
    public void testSender2() throws Exception{
        Order order = new Order();
        order.setId("001");
        order.setName("测试订单");
        this.rabbitSender.sendOrder(order);
    }

}
