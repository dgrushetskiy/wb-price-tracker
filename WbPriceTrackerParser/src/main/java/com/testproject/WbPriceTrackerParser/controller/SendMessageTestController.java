package com.testproject.WbPriceTrackerParser.controller;

import com.testproject.WbPriceTrackerParser.dto.PriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test/messages")
public class SendMessageTestController {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public SendMessageTestController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    //Testing message sending
    @PostMapping()
    public ResponseEntity<?> sendPrice(@RequestBody PriceDto priceDto) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, priceDto);
        log.info("Message sent successfully");
        return new ResponseEntity<>("Message sent successfully", HttpStatus.OK);
    }
}
