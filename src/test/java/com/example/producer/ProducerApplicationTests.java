package com.example.producer;

import com.example.producer.entity.Order;
import com.example.producer.entity.Packaged;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@SpringBootTest
class ProducerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Test
    public void mq(){
        //创建交换机
        rabbitAdmin.declareExchange(new DirectExchange("test.direct",false,false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic",false,false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout",false,false));
        //创建队列
        rabbitAdmin.declareQueue(new Queue("test.direct.queue",false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue",false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue",false));
        //direct绑定
        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
                Binding.DestinationType.QUEUE,
                "test.direct",
                "direct",
                null
                ));
        //topic绑定
        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.topic.queue",false))
                        .to(new TopicExchange("test.topic",false,false))
                        .with("user.#")
        );
        //fanout绑定
        rabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue("test.fanout.queue",false))
                .to(new FanoutExchange("test.fanout",false,false))
        );
        //清空指定队列消息
        rabbitAdmin.purgeQueue("test.topic.queue",false);

    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage() throws Exception{
        //创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc","信息描述");
        messageProperties.getHeaders().put("type","自定义消费者");
        Message message = new Message("Hello RabbitMQ".getBytes(),messageProperties);
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                System.err.println("-----------添加额外的设置-----------");
                message.getMessageProperties().getHeaders().put("desc","额外修改的信息描述");
                message.getMessageProperties().getHeaders().put("attr","添加额外得设置");
                return message;
            }
        });
    }

    @Test
    public void testSendMessage2() throws Exception{
        //创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq消息1234".getBytes(),messageProperties);
        rabbitTemplate.send("topic001","spring.abc",message);
        rabbitTemplate.convertAndSend("topic001","spring.ampq","hello object message send11");
        rabbitTemplate.convertAndSend("topic002","rabbit.abc","hello object message send22");

    }
    @Test
    public void testSendMessage4Text() throws Exception{
        //创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("mq消息1234".getBytes(),messageProperties);
        rabbitTemplate.send("topic001","spring.abc",message);
        rabbitTemplate.send("topic002","rabbit.abc",message);
    }

    @Test
    public void testSendJsonMessage() throws JsonProcessingException {
        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 Json: "+json);

        MessageProperties messageProperties = new MessageProperties();
        //一定要设置contenType为application/json
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(),messageProperties);
        rabbitTemplate.send("topic001","spring.order",message);

    }

    @Test
    public void testSendJavaMessage() throws JsonProcessingException {
        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 java: "+json);

        MessageProperties messageProperties = new MessageProperties();
        //一定要设置contenType为application/json
        messageProperties.setContentType("application/json");
        //com.example.producer.entity.Order
        messageProperties.getHeaders().put("__TypeId__", "com.example.producer.entity.Order");
        Message message = new Message(json.getBytes(),messageProperties);
        rabbitTemplate.send("topic001","spring.order",message);

    }

    @Test
    public void testSendMappingMessage() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Order order = new Order();
        order.setId("001");
        order.setName("订单消息");
        order.setContent("订单描述消息");
        String json1 = mapper.writeValueAsString(order);
        System.err.println("order 4 json: "+json1);

        MessageProperties messageProperties1 = new MessageProperties();
        //这里注意一定要修改contentType为application/json
        messageProperties1.setContentType("application/json");
        messageProperties1.getHeaders().put("__TypeId__","order");
        Message message1 = new Message(json1.getBytes(),messageProperties1);
        rabbitTemplate.send("topic001","spring.order",message1);

        Packaged pack = new Packaged();
        pack.setId("002");
        pack.setName("包裹消息");
        pack.setDescription("包裹描述消息");

        String json2 = mapper.writeValueAsString(pack);
        System.err.println("pack 4 json : "+json2);

        MessageProperties messageProperties2 = new MessageProperties();
        //这里注意一定要修改contentType为application/json
        messageProperties2.setContentType("application/json");
        messageProperties2.getHeaders().put("__TypeId__","packaged");
        Message message2 = new Message(json2.getBytes(),messageProperties2);
        rabbitTemplate.send("topic001","spring.order",message2);
    }

    @Test
    public void TestSendExtConverterMessage() throws IOException {
//        byte[] body = Files.readAllBytes(Paths.get("C:\\Users\\18310\\Pictures\\Saved Pictures","2.png"));
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setContentType("image/png");
//        messageProperties.getHeaders().put("extName","png");
//        Message message  = new Message(body,messageProperties);
//        rabbitTemplate.send("","image_queue",message);
//        byte[] body = Files.readAllBytes(Paths.get("E:\\002_test", "hello.pdf"));
        byte[] body1 = Files.readAllBytes(Paths.get("E:\\002_test","hello.pdf"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/pdf");
        Message message = new Message(body1,messageProperties);
        rabbitTemplate.send("","pdf_queue",message);

    }

}
