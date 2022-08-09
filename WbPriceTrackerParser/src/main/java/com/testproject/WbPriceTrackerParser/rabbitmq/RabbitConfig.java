package com.testproject.WbPriceTrackerParser.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testproject.WbPriceTrackerParser.dto.PriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableRabbit
@Configuration
public class RabbitConfig {

    private final ObjectMapper objectMapper;

    //from app.properties
    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    @Value("${spring.rabbitmq.host}")
    private String hostName;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Autowired
    public RabbitConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // create Queue
    @Bean
    public Queue parserQueue() {
        return new Queue(queueName, false);
    }

    // create DirectExchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    // binding Queue and DirectExchange with Routing key
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey);
    }

    // MessageConverter for Json format
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // RabbitTemplate send messages to the Queue
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setBeforePublishPostProcessors(m -> {
            m.getMessageProperties().setHeader("__TypeId__", PriceDto.class.getSimpleName());
            return m;
        });
        return rabbitTemplate;
    }

    // ConnectionFactory to make a connection to RabbitMQ
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(hostName);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    // RabbitAdmin to automatically declare and bound queues
    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
}
