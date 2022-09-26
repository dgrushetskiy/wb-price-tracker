package com.testproject.WbPriceTrackerParser.integration;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.testproject.WbPriceTrackerParser.dto.PriceDto;
import com.testproject.WbPriceTrackerParser.exception.RequestException;
import com.testproject.WbPriceTrackerParser.service.ParserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
public class ParserServiceIT {

    private static final String errorMsgForEmptyWbResponse = "Failed to define item price";

    private static final Long EXISTING_CODE = 15061503L;
    private static final Long NOT_EXISTING_CODE = 20381112L;
    private static final Long CODE_WITHOUT_PRICE = 14880730L;

    private final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    private final ParserService parserService;

    @SneakyThrows
    @Test
    void updatePriceExistingCode() {
        String json = parserService.getJsonFromWb(EXISTING_CODE);
        Map<String, Object> updatedPrice = parserService.parsePrice(json, EXISTING_CODE);
        PriceDto jsonDataForRequest = parserService.buildJsonDataForRequest(updatedPrice);

        assertEquals(EXISTING_CODE, JsonPath.using(conf).parse(json).read("$.data.products[0].id", Long.class));
        assertEquals("Ticle", JsonPath.using(conf).parse(json).read("$.data.products[0].brand", String.class));
        assertEquals("Футболка женская оверсайз", JsonPath.using(conf).parse(json).read("$.data.products[0].name", String.class));

        assertEquals(EXISTING_CODE, updatedPrice.get("code"));
        assertNotNull(updatedPrice.get("price"));

        assertEquals(EXISTING_CODE, jsonDataForRequest.getCode());
        assertNotNull(jsonDataForRequest.getPrice());
    }

    @SneakyThrows
    @Test
    void updatePriceNotExistingCode() {
        String json = parserService.getJsonFromWb(NOT_EXISTING_CODE);
        RequestException requestException = assertThrows(RequestException.class, () -> parserService.parsePrice(json, EXISTING_CODE));

        assertTrue(requestException.getMessage().startsWith(errorMsgForEmptyWbResponse));
    }

    @SneakyThrows
    @Test
    void updatePriceCodeWithoutPrice() {
        String json = parserService.getJsonFromWb(CODE_WITHOUT_PRICE);
        RequestException requestException = assertThrows(RequestException.class, () -> parserService.parsePrice(json, CODE_WITHOUT_PRICE));

        assertTrue(requestException.getMessage().startsWith(errorMsgForEmptyWbResponse));
    }
}
