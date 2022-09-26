package com.testproject.WbPriceTrackerApi.util;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.testproject.WbPriceTrackerApi.exception.ExceptionMessage;
import com.testproject.WbPriceTrackerApi.exception.MessageConstant;
import com.testproject.WbPriceTrackerApi.exception.RequestException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Setter
@Getter
@Component
public class ParserUtil {

    //from properties
    @Value("${url.wb}")
    private String URL_WB;
    @Value("${jsonpath.brand}")
    private String JSONPATH_BRAND;
    @Value("${jsonpath.name}")
    private String JSONPATH_NAME;
    @Value("${jsonpath.salePriceU}")
    private String JSONPATH_PRICE;
    private static final String JSONPATH_DATA = "$.data.products[*]";

    // config for JsonPath to return null instead of exception while parsing
    private Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    private final RestTemplate restTemplate;

    @Autowired
    public ParserUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getInfoFromWb(Long code) {
        String json = getJson(code);
        String brand = getBrand(json);
        String name = getName(json);
        Integer price = getPrice(json);

        return Map.of("brand", brand, "name", name, "price", price);
    }

    public String getJson(Long code) {
        try {
            return restTemplate.getForObject(URL_WB + code, String.class);
        } catch (RestClientException e) {
            log.error("Error while making GET request to {} to get item info for item code {}. " +
                    "Message: {}", URL_WB + code, code, e.getMessage());
            throw new RequestException(ExceptionMessage.setMessage(MessageConstant.PARSER_FAIL_ADDED_CODE, String.valueOf(code)),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public String getBrandFromWb(String json) {
        // null may be returned
        return JsonPath.using(conf).parse(json).read(JSONPATH_DATA, List.class).size() == 0 ? null :
                JsonPath.using(conf).parse(json).read(JSONPATH_BRAND);
    }

    private String getBrand(String json) {
        String brandFromWb = getBrandFromWb(json);
        if (brandFromWb == null) {
            log.warn("Add Item In Profile Method Called. Fail while parsing JSON {}. Failed to get item brand.", json);
            throw new RequestException(ExceptionMessage.setMessage(MessageConstant.PARSER_FAIL_DEFINE_BRAND), HttpStatus.BAD_REQUEST);
        }
        return brandFromWb;
    }

    public String getNameFromWb(String json) {
        // null may be returned
        return JsonPath.using(conf).parse(json).read(JSONPATH_DATA, List.class).size() == 0 ? null :
                JsonPath.using(conf).parse(json).read(JSONPATH_NAME);
    }

    private String getName(String json) {
        String nameFromWb = getNameFromWb(json);
        if (nameFromWb == null) {
            log.warn("Add Item In Profile Method Called. Fail while parsing JSON {}. Failed to get item name.", json);
            throw new RequestException(ExceptionMessage.setMessage(MessageConstant.PARSER_FAIL_DEFINE_NAME), HttpStatus.BAD_REQUEST);
        }
        return nameFromWb;
    }

    public String getPriceFromWb(String json) {
        // null may be returned
        return JsonPath.using(conf).parse(json).read(JSONPATH_DATA, List.class).size() == 0 ? null :
                JsonPath.using(conf).parse(json).read(JSONPATH_PRICE, String.class);
    }

    private Integer getPrice(String json) {
        String priceFromWb = getPriceFromWb(json);
        if (priceFromWb == null) {
            log.warn("Add Item In Profile Method Called. Fail while parsing JSON {}. Failed to get item price.", json);
            throw new RequestException(ExceptionMessage.setMessage(MessageConstant.PARSER_FAIL_DEFINE_PRICE), HttpStatus.BAD_REQUEST);
        }
        return Integer.parseInt(priceFromWb) / 100;
    }
}
