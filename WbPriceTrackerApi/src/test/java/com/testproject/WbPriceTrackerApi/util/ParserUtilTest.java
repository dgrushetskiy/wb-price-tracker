package com.testproject.WbPriceTrackerApi.util;

import com.testproject.WbPriceTrackerApi.exception.RequestException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParserUtilTest {

    private static final String URL_WB = "https://card.wb.ru/cards/detail?locale=ru&curr=rub&nm=";
    private static final String JSONPATH_BRAND = "$.data.products[0].brand";
    private static final String JSONPATH_NAME = "$.data.products[0].name";
    private static final String JSONPATH_PRICE = "$.data.products[0].salePriceU";

    private static final Long ITEM_CODE = 15061503L;
    private static final String ITEM_BRAND = "Ticle";
    private static final String ITEM_NAME = "Футболка";
    private static final Integer ITEM_PRICE = 1176;
    private static final Map<String, Object> infoFromServer =
            Map.of("brand", ITEM_BRAND, "name", ITEM_NAME, "price", ITEM_PRICE);

    private static final String errorMsg = "Failed to add item code";

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ParserUtil parserUtil;

    @BeforeEach
    public void setup() {
        parserUtil.setURL_WB(URL_WB);
        parserUtil.setJSONPATH_BRAND(JSONPATH_BRAND);
        parserUtil.setJSONPATH_NAME(JSONPATH_NAME);
        parserUtil.setJSONPATH_PRICE(JSONPATH_PRICE);
    }

    @SneakyThrows
    @Test
    void getInfoFromWb() {
        URI uri = ClassLoader.getSystemResource("wbResponse").toURI();
        String wbResponse = Files.readString(Paths.get(uri));

        doReturn(wbResponse).when(restTemplate).getForObject(URL_WB + ITEM_CODE, String.class);

        Map<String, Object> actualResult = parserUtil.getInfoFromWb(ITEM_CODE);
        assertEquals(infoFromServer.get("brand"), actualResult.get("brand"));
        assertEquals(infoFromServer.get("name"), actualResult.get("name"));
        assertEquals(infoFromServer.get("price"), actualResult.get("price"));

        verify(restTemplate, times(1)).getForObject(URL_WB + ITEM_CODE, String.class);
    }

    @SneakyThrows
    @Test
    void getInfoFromWbThrowsRestClientException() {
        doThrow(RestClientException.class).when(restTemplate).getForObject(URL_WB + ITEM_CODE, String.class);
        RequestException requestException = assertThrows(RequestException.class, () -> parserUtil.getInfoFromWb(ITEM_CODE));

        assertTrue(requestException.getMessage().startsWith(errorMsg));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());
        verify(restTemplate, times(1)).getForObject(URL_WB + ITEM_CODE, String.class);
    }
}