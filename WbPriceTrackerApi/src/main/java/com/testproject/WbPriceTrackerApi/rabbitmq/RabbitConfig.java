package com.testproject.WbPriceTrackerApi.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testproject.WbPriceTrackerApi.dto.PriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableRabbit
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

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

    @Value("${rabbit.messageHeader.typeId}")
    private String typeId;


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
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter(objectMapper);
        jsonConverter.setClassMapper(classMapper());
        return jsonConverter;
    }

    // ClassMapper to define the class to convert from message TypeId
    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(typeId, PriceDto.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    // RabbitTemplate send messages to the Queue
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
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

    // create message listener container
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(messageConverter());
        factory.setErrorHandler(errorHandler());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        rabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }

    //Error when an exception is thrown by the listener
    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(new FatalExceptionStrategyImpl());
    }

    public static class FatalExceptionStrategyImpl extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {
        @Override
        public boolean isFatal(Throwable throwable) {
            if (throwable instanceof ListenerExecutionFailedException) {
                ListenerExecutionFailedException exception = (ListenerExecutionFailedException) throwable;
                log.error("Failed to process inbound message from queue : {}; \n " +
                                "Failed message: {}; \n " +
                                "Error Message: {}; \n " +
                                "Error Cause : {}",
                        exception.getFailedMessage().getMessageProperties().getConsumerQueue(),
                        exception.getFailedMessage(),
                        exception.getMessage(),
                        exception.getCause());
            }
            return super.isFatal(throwable);
        }
    }
}
















