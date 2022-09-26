package com.testproject.WbPriceTrackerParser.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.testproject.WbPriceTrackerParser.dto.ItemCodeDto;
import com.testproject.WbPriceTrackerParser.dto.ParserResponse;
import com.testproject.WbPriceTrackerParser.dto.PriceDto;
import com.testproject.WbPriceTrackerParser.exception.RequestException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParserServiceTest {

    private static final String URL_APP = "http://localhost:8085/api/v1/parser";
    private static final String URL_WB = "https://card.wb.ru/cards/detail?locale=ru&curr=rub&nm=";
    private static final String JSONPATH_PRICE = "$.data.products[0].salePriceU";
    private static final String exchangeName = "directExchange";
    private static final String routingKey = "parserRoutingKey";

    private static final Long ITEM_CODE1 = 15061503L;
    private static final String CODE1_BRAND = "Ticle";
    private static final Integer CODE1_PRICE = 1176;
    private static final Long ITEM_CODE2 = 32956137L;

    private static final List<ItemCodeDto> codes = List.of(
            ItemCodeDto.builder().code(ITEM_CODE1).build(),
            ItemCodeDto.builder().code(ITEM_CODE2).build());

    private static final Map<String, Object> price = Map.of("code", ITEM_CODE1, "price", CODE1_PRICE);

    private static final PriceDto jsonData = PriceDto.builder().code(ITEM_CODE1).price(CODE1_PRICE).build();

    private static final String errorMsgForGetRequest = "Error while making GET request";
    private static final String errorMsgForEmptyWbResponse = "Failed to define item price";

    private final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private ParserService parserService;

    @BeforeEach
    public void setup() {
        parserService.setUrlApp(URL_APP);
        parserService.setUrlWb(URL_WB);
        parserService.setJsonpathPrice(JSONPATH_PRICE);
        parserService.setExchangeName(exchangeName);
        parserService.setRoutingKey(routingKey);
    }

    @Test
    void getListOfItemsCodesFromApp() {
        doReturn(new ParserResponse(codes)).when(restTemplate).getForObject(URL_APP, ParserResponse.class);
        List<Long> actualResult = parserService.getListOfItemsCodesFromApp();

        assertEquals(codes.size(), actualResult.size());
        assertEquals(ITEM_CODE1, actualResult.get(0));
        assertEquals(ITEM_CODE2, actualResult.get(1));

        verify(restTemplate, times(1)).getForObject(URL_APP, ParserResponse.class);
    }

    @Test
    @Disabled
    void getListOfItemsCodesFromAppThrowsExceptionIfResponseIsEmpty() {
        doReturn(null).when(restTemplate).getForObject(URL_APP, ParserResponse.class);
        RequestException requestException = assertThrows(RequestException.class, () -> parserService.getListOfItemsCodesFromApp());

        assertTrue(requestException.getMessage().startsWith(errorMsgForGetRequest));
        verify(restTemplate, times(1)).getForObject(URL_APP, ParserResponse.class);
    }

    @Test
    @Disabled
    void getListOfItemsCodesFromAppThrowsExceptionIfListInResponseIsEmpty() {
        doReturn(new ParserResponse()).when(restTemplate).getForObject(URL_APP, ParserResponse.class);
        RequestException requestException = assertThrows(RequestException.class, () -> parserService.getListOfItemsCodesFromApp());

        assertTrue(requestException.getMessage().startsWith(errorMsgForGetRequest));
        verify(restTemplate, times(1)).getForObject(URL_APP, ParserResponse.class);
    }

    @SneakyThrows
    @Test
    void getJsonFromWb() {
        URI uri = ClassLoader.getSystemResource("wbResponse").toURI();
        String wbResponse = Files.readString(Paths.get(uri));

        doReturn(wbResponse).when(restTemplate).getForObject(URL_WB + ITEM_CODE1, String.class);
        String actualResult = parserService.getJsonFromWb(ITEM_CODE1);

        assertEquals(ITEM_CODE1, JsonPath.using(conf).parse(actualResult).read("$.data.products[0].id", Long.class));
        assertEquals(CODE1_BRAND, JsonPath.using(conf).parse(actualResult).read("$.data.products[0].brand", String.class));
        assertEquals(CODE1_PRICE, JsonPath.using(conf).parse(actualResult).read(JSONPATH_PRICE, Integer.class) / 100);
        verify(restTemplate, times(1)).getForObject(URL_WB + ITEM_CODE1, String.class);
    }

    @SneakyThrows
    @Test
    @Disabled
    void getJsonFromWbThrowsException() {
        doThrow(RuntimeException.class).when(restTemplate).getForObject(URL_WB + ITEM_CODE1, String.class);
        RequestException requestException = assertThrows(RequestException.class, () -> parserService.getJsonFromWb(ITEM_CODE1));

        assertTrue(requestException.getMessage().startsWith(errorMsgForGetRequest));
    }

    @SneakyThrows
    @Test
    void parsePrice() {
        URI uri = ClassLoader.getSystemResource("wbResponse").toURI();
        String wbResponse = Files.readString(Paths.get(uri));

        Map<String, Object> actualResult = parserService.parsePrice(wbResponse, ITEM_CODE1);
        assertEquals(ITEM_CODE1, actualResult.get("code"));
        assertEquals(CODE1_PRICE, actualResult.get("price"));
    }

    @SneakyThrows
    @Test
    @Disabled
    void parsePriceThrowsExceptionIfResponseIsEmpty() {
        URI uri = ClassLoader.getSystemResource("wbEmptyResponse").toURI();
        String wbResponse = Files.readString(Paths.get(uri));

        RequestException requestException = assertThrows(RequestException.class,
                () -> parserService.parsePrice(wbResponse, ITEM_CODE1));

        assertTrue(requestException.getMessage().startsWith(errorMsgForEmptyWbResponse));
    }

    @Test
    void buildJsonDataForRequest() {
        PriceDto actualResult = parserService.buildJsonDataForRequest(price);
        assertEquals(ITEM_CODE1, actualResult.getCode());
        assertEquals(CODE1_PRICE, actualResult.getPrice());
    }

    @Test
    void sendMsgToRabbitMqWithJsonData() {
        parserService.sendMsgToRabbitMqWithJsonData(jsonData);
        verify(rabbitTemplate, times(1)).convertAndSend(exchangeName, routingKey, jsonData);
    }

    @Test
    void sendMsgToRabbitMqWithJsonDataThrowsAmqpException() {
        doThrow(AmqpException.class).when(rabbitTemplate).convertAndSend(exchangeName, routingKey, jsonData);
        assertThrows(RequestException.class, () -> parserService.sendMsgToRabbitMqWithJsonData(jsonData));
        verify(rabbitTemplate, times(1)).convertAndSend(exchangeName, routingKey, jsonData);
    }
}
