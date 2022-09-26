package com.testproject.WbPriceTrackerParser.service;

import com.testproject.WbPriceTrackerParser.exception.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class ScheduledPricesUpdateService {

    private final ParserService parserService;
    private final AsyncPriceUpdateService asyncPriceUpdateService;

    public ScheduledPricesUpdateService(ParserService parserService, AsyncPriceUpdateService asyncPriceUpdateService) {
        this.parserService = parserService;
        this.asyncPriceUpdateService = asyncPriceUpdateService;
    }

    @Scheduled(initialDelayString = "${scheduler.initialDelay}", fixedRateString = "${scheduler.fixedRate}")
    public void updateItemsPrices() {
        log.info("Update Items Prices start at: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(MessageConstant.DATE_TIME_PATTERN)));

        List<Long> codesFromApp = parserService.getListOfItemsCodesFromApp();

        if (!codesFromApp.isEmpty()) {
            codesFromApp.forEach(asyncPriceUpdateService::updatePrice);
        } else {
            log.warn("List of codes from App is empty. No data to update");
        }

        log.info("Update Items Prices end at: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(MessageConstant.DATE_TIME_PATTERN)));
    }
}
