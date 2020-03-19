package com.example.producer.config;

import com.example.producer.adapter.MessageDelegate;
import com.example.producer.constant.RabbitMqConstant;
import com.example.producer.convert.ImageMessageConverter;
import com.example.producer.convert.PDFMessageConverter;
import com.example.producer.convert.TextMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * @author LXJ
 * @description
 * @date 2020/3/6 18:44
 */
@Configuration
public class RabbitMqConfig {
    @Autowired
    private RabbitMqConstant rabbitMqConstant;

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(rabbitMqConstant.getAddresses());
        connectionFactory.setVirtualHost(rabbitMqConstant.getVirtualHost());
        connectionFactory.setUsername(rabbitMqConstant.getUsername());
        connectionFactory.setPassword(rabbitMqConstant.getPassword());
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public TopicExchange exchange001(){
        return new TopicExchange("topic001",true,false);
    }

    @Bean
    public Queue queue001(){
        return new Queue("queue001",true);
    }

    @Bean
    public Binding binding001(){
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002(){
        return new TopicExchange("topic002",true,false);
    }

    @Bean
    public Queue queue002(){
        return new Queue("queue002",true);
    }

    @Bean
    public Binding binding002(){
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003(){
        return new Queue("queue003",true);
    }

    @Bean
    public Binding binding003(){
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image(){
        return new Queue("image_queue",true);
    }

    @Bean
    public Queue queue_pdf(){
        return new Queue("pdf_queue",true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //监控队列
        container.setQueues(queue001(),queue002(),queue003(),queue_image(),queue_pdf());
        //设置消费者数量
        container.setConcurrentConsumers(1);
        //设置最大消费者数量
        container.setMaxConcurrentConsumers(5);
        //设置是否重回队列
        container.setDefaultRequeueRejected(false);
        //设置签收模式 自动/手动签收
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue+"_"+ UUID.randomUUID().toString();
            }
        });
        //设置消息监听
//        container.setMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                String msg = new String(message.getBody());
//                System.err.println("--------消费者:------"+msg);
//            }
//        });
        //1.适配器模式添加监听 默认手自己的方法名字的： handleMessage
        //可以自己制定一个方法的名字：consumeMessage
        //也可以添加一个转换器:从字节数组转换为String
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(new TextMessageConverter());
//        container.setMessageListener(adapter);
        //2.适配器方式：我们的队列名称和方法名称也可以进行一一的匹配
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        Map<String,String> queueOrTagToMethodName = new HashMap<>();
//        queueOrTagToMethodName.put("queue001","method1");
//        queueOrTagToMethodName.put("queue002","method2");
//        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
//        container.setMessageListener(adapter);
        //1.1支持json格式的转换器
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);

        //1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        //添加可信任的包
//        javaTypeMapper.addTrustedPackages("*");
//        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);

        //1.3DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        Map<String,Class<?>> idClassMapping = new HashMap<>();
//        idClassMapping.put("order",com.example.producer.entity.Order.class);
//        idClassMapping.put("packaged",com.example.producer.entity.Packaged.class);
//        javaTypeMapper.setIdClassMapping(idClassMapping);
//        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);
        //1.4 ext conver
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        //全局转换器
        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textConvert = new TextMessageConverter();
        converter.addDelegate("text",textConvert);
        converter.addDelegate("html/text",textConvert);
        converter.addDelegate("xml/text",textConvert);
        converter.addDelegate("text/plain",textConvert);

        Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
        converter.addDelegate("json",jsonConvert);
        converter.addDelegate("application/json",jsonConvert);

        ImageMessageConverter imageConvert = new ImageMessageConverter();
        converter.addDelegate("image/png",imageConvert);
        converter.addDelegate("image",imageConvert);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();
        converter.addDelegate("application/pdf",pdfConverter);

        adapter.setMessageConverter(converter);
        container.setMessageListener(adapter);

        return container;
    }


}
