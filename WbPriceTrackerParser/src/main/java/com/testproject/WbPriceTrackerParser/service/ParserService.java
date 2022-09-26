package com.testproject.WbPriceTrackerParser.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.testproject.WbPriceTrackerParser.dto.ItemCodeDto;
import com.testproject.WbPriceTrackerParser.dto.ParserResponse;
import com.testproject.WbPriceTrackerParser.dto.PriceDto;
import com.testproject.WbPriceTrackerParser.exception.ExceptionMessage;
import com.testproject.WbPriceTrackerParser.exception.MessageConstant;
import com.testproject.WbPriceTrackerParser.exception.RequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ParserService {

    @Value("${url.app}")
    private String URL_APP;
    @Value("${url.wb}")
    private String URL_WB;
    @Value("${jsonpath.salePriceU}")
    private String JSONPATH_PRICE;
    private static final String JSONPATH_DATA = "$.data.products[*]";

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    // config for JsonPath to return null instead of exception while parsing
    private final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);


    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ParserService(RestTemplate restTemplate, RabbitTemplate rabbitTemplate) {
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Long> getListOfItemsCodesFromApp() {
        try {
            ParserResponse jsonResponse = restTemplate.getForObject(URL_APP, ParserResponse.class);
            if (jsonResponse == null || jsonResponse.getCodes() == null) {
                throw new RequestException(ExceptionMessage.setMessage(MessageConstant.APP_ERR_REQUEST,URL_APP));
            }
            return jsonResponse.getCodes().stream().map(ItemCodeDto::getCode).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error Message: {}", e.getMessage());
            throw new RequestException(e.getMessage());
        }
    }

    public String getJsonFromWb(Long code) {
        try {
            return restTemplate.getForObject(URL_WB + code, String.class);
        } catch (Exception e) {
            log.error("Error while making GET request to {} to get item info for item code {}. " +
                    "Message: {}", URL_WB + code, code, e.getMessage());
            throw new RequestException(ExceptionMessage.setMessage(MessageConstant.WB_ERR_REQUEST, URL_WB, String.valueOf(code), String.valueOf(code)));
        }
    }
    private String getPriceFromWb(String json) {
        // null may be returned
        return JsonPath.using(conf).parse(json).read(JSONPATH_DATA, List.class).size() == 0 ? null :
                JsonPath.using(conf).parse(json).read(JSONPATH_PRICE, String.class);
    }

    public Map<String, Object> parsePrice(String json, Long code) {
        String priceFromWb = getPriceFromWb(json);
        if (priceFromWb == null) {
            log.warn("Fail while parsing JSON {}. Failed to get item {} price.", json, code);
            throw new RequestException(ExceptionMessage.setMessage(MessageConstant.ERR_DEFINE_PRICE, String.valueOf(code)));
        }
        Integer price = Integer.parseInt(priceFromWb) / 100;
        return Map.of("code", code, "price", price);
    }

    public PriceDto buildJsonDataForRequest(Map<String, Object> price) {
        return PriceDto.builder()
                .code((Long) price.get("code"))
                .price((Integer) price.get("price"))
                .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern(MessageConstant.DATE_TIME_PATTERN)))
                .build();
    }

    public void sendMsgToRabbitMqWithJsonData(PriceDto jsonData) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonData);
        } catch (AmqpException e) {
            log.error("Error while sending message to RabbitMq : exchange [{}]; routing key: [{}]; json {}. \n" +
                            "Message: {}; \n" +
                            "Cause: {}; \n",
                    exchangeName, routingKey, jsonData,
                    e.getMessage(), e.getCause());
            throw new RequestException(e.getMessage());
        }
    }

    public void setUrlApp(String UrlApp) {
        this.URL_APP = UrlApp;
    }

    public void setUrlWb(String UrlWb) {
        this.URL_WB = UrlWb;
    }

    public void setJsonpathPrice(String jsonpathPrice) {
        this.JSONPATH_PRICE = jsonpathPrice;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}

    //prev version with POST request to Api with updated prices instead of using RabbitMQ

//    private void makePostRequestWithJsonData(String urlRequest, Object jsonData) {
//        final HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Object> request = new HttpEntity<>(jsonData, headers);
//        try {
//            restTemplate.postForObject(urlRequest, request, String.class);
//        } catch (HttpClientErrorException e) {
//            log.error("Error while making POST request to {} with json {}. " +
//                    "Message: {}; \n" +
//                    "Cause: {}; \n",
//                    urlRequest, jsonData, e.getMessage(), e.getCause());
//        }
//    }
