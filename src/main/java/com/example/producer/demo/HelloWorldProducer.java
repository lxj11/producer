package com.example.producer.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author LXJ
 * @description
 * @date 2020/3/3 20:09
 */
public class HelloWorldProducer {
    private static final String exchange_name = "hello_exchange";
    private static final String queue_name = "hello_queue";
    private static final String routing_key = "routingkey_hello";
    private static final String ip_address = "127.0.0.1";
    private static final int port = 5672;
    private static final String username = "guest";
    private static final String password = "guest";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ip_address);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        //创建连接
        Connection connection = connectionFactory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        //创建交换机
        channel.exchangeDeclare(exchange_name,"" +
                "",true,false,null);
        //创建队列
        channel.queueDeclare(queue_name,true,false,false,null);
        //队列和交换机根据路由key绑定
        channel.queueBind(queue_name,exchange_name,routing_key);
        String message = "Hello world！";
        //发送消息
        channel.basicPublish(exchange_name,routing_key,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
        //关闭资源
        channel.close();
        connection.close();


    }
}
