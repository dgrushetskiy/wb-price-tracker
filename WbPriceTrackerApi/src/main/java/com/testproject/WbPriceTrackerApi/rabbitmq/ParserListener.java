package com.testproject.WbPriceTrackerApi.rabbitmq;

import com.testproject.WbPriceTrackerApi.dto.PriceDto;
import com.testproject.WbPriceTrackerApi.model.Price;
import com.testproject.WbPriceTrackerApi.service.PriceService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Slf4j
@Component
@RabbitListener(queues = "${rabbitmq.queue.name}")
public class ParserListener {

    private final DtoMapper dtoMapper;
    private final PriceService priceService;

    @Autowired
    public ParserListener(DtoMapper dtoMapper, PriceService priceService) {
        this.dtoMapper = dtoMapper;
        this.priceService = priceService;
    }

    @RabbitHandler
    public void parserListener(@Valid @Payload PriceDto priceDto) {

        Price price = dtoMapper.convertToPrice(priceDto);
        priceService.addPriceFromParser(price);
    }
}
