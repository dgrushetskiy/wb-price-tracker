package com.testproject.WbPriceTrackerParser.service;

import com.testproject.WbPriceTrackerParser.dto.PriceDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AsyncPriceUpdateService {

    private final ParserService parserService;

    public AsyncPriceUpdateService(ParserService parserService) {
        this.parserService = parserService;
    }

    @Async
    public void updatePrice(Long code) {
        String json = parserService.getJsonFromWb(code);
        Map<String, Object> updatedPrice = parserService.parsePrice(json, code);
        PriceDto jsonDataForRequest = parserService.buildJsonDataForRequest(updatedPrice);
        parserService.sendMsgToRabbitMqWithJsonData(jsonDataForRequest);
    }
}
