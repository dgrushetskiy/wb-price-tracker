package com.testproject.WbPriceTrackerParser.service;

import com.testproject.WbPriceTrackerParser.dto.PriceDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AsyncPriceUpdateServiceTest {

    private static final Long ITEM_CODE1 = 15061503L;
    private static final Integer CODE1_PRICE = 1000;
    private static final Map<String, Object> price1 = Map.of("code", ITEM_CODE1, "price", CODE1_PRICE);
    private static final PriceDto jsonDataForRequest1 = PriceDto.builder().code(ITEM_CODE1).price(CODE1_PRICE).build();

    private static final Long ITEM_CODE2 = 32956137L;
    private static final Integer CODE2_PRICE = 1500;
    private static final Map<String, Object> price2 = Map.of("code", ITEM_CODE2, "price", CODE2_PRICE);
    private static final PriceDto jsonDataForRequest2 = PriceDto.builder().code(ITEM_CODE2).price(CODE2_PRICE).build();

    private static final List<Long> codesFromApp = List.of(ITEM_CODE1, ITEM_CODE2);

    @Mock
    private ParserService parserService;
    @InjectMocks
    private AsyncPriceUpdateService asyncPriceUpdateService;

    @SneakyThrows
    @Test
    void updatePrice() {
        URI uri = ClassLoader.getSystemResource("wbResponse").toURI();
        String wbResponse = Files.readString(Paths.get(uri));

        doReturn(wbResponse).when(parserService).getJsonFromWb(ITEM_CODE1);
        doReturn(price1).when(parserService).parsePrice(wbResponse, ITEM_CODE1);
        doReturn(jsonDataForRequest1).when(parserService).buildJsonDataForRequest(price1);

        asyncPriceUpdateService.updatePrice(ITEM_CODE1);

        verify(parserService, times(1)).getJsonFromWb(anyLong());
        verify(parserService, times(1)).parsePrice(any(String.class), anyLong());
        verify(parserService, times(1)).buildJsonDataForRequest(anyMap());
        verify(parserService, times(1)).sendMsgToRabbitMqWithJsonData(any(PriceDto.class));
    }
}