package com.testproject.WbPriceTrackerApi.integration.service;

import com.testproject.WbPriceTrackerApi.dto.GetItemPricesDto;
import com.testproject.WbPriceTrackerApi.dto.PriceFilter;
import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.integration.IntegrationTestBase;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.model.Price;
import com.testproject.WbPriceTrackerApi.repository.ItemRepository;
import com.testproject.WbPriceTrackerApi.service.PriceService;
import com.testproject.WbPriceTrackerApi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PriceServiceIT extends IntegrationTestBase {

    private static final Long USER6_ID = 6L;

    private static final Long ITEM_CODE = 15061497L;
    private static final Long NON_EXISTENT_ITEM_CODE = 1000000L;
    private static final Long ITEM_CODE_NOT_PRESENT_FOR_USER6 = 12052239L;

    private static final Integer ITEM_PRICE = 1000;

    private static final LocalDate ITEM_DATE = LocalDate.of(2022, 8, 1);
    private static final LocalTime ITEM_TIME = LocalTime.of(12, 0, 0);

    private static final Price price = Price.builder().item(Item.builder().code(ITEM_CODE).prices(new ArrayList<>()).build())
            .price(ITEM_PRICE).date(LocalDateTime.of(ITEM_DATE, ITEM_TIME)).build();
    private static final Price priceForException = Price.builder().item(Item.builder().code(NON_EXISTENT_ITEM_CODE)
                    .prices(new ArrayList<>()).build()).price(ITEM_PRICE).date(LocalDateTime.of(ITEM_DATE, ITEM_TIME)).build();

    private static final String errorMsgAddPrice = "Fail while adding updated price from parser";
    private static final String errorMsgFindPrices = "not found in user profile";

    private final PriceService priceService;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Test
    void addPriceFromParser() {
        priceService.addPriceFromParser(price);

        assertTrue(price.getItem().getPrices().contains(price));
        price.getItem().getPrices().stream().filter(p -> p.equals(price)).findAny()
                .ifPresent(actual -> assertEquals(ITEM_PRICE, actual.getPrice()));
        assertTrue(itemRepository.findByCode(ITEM_CODE).isPresent());
        assertTrue(itemRepository.findByCode(ITEM_CODE).get().getPrices().contains(price));
    }

    @Test
    void addPriceFromParserThrowsExceptionIfItemNotFound() {
        RequestException requestException = assertThrows(RequestException.class,
                () -> priceService.addPriceFromParser(priceForException));

        assertTrue(requestException.getMessage().startsWith(errorMsgAddPrice));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());
     }

    @Test
    void findAllItemPricesWithEmptyFilter() {
        PriceFilter priceFilter = PriceFilter.builder().build();
        List<GetItemPricesDto> actualResult = priceService.findAllItemPrices(
                userService.findById(USER6_ID), ITEM_CODE, priceFilter);

        assertEquals(2, actualResult.size());
        //findAllItemPrices() -> ORDER BY date DESC
        assertEquals(LocalDate.of(2022, 7, 28), actualResult.get(0).getDate().toLocalDate());
        assertEquals(1100, actualResult.get(0).getPrice());
        assertEquals(LocalDate.of(2022, 7, 27), actualResult.get(1).getDate().toLocalDate());
        assertEquals(1176, actualResult.get(1).getPrice());
    }

    @Test
    void findAllItemPricesWithFromDateFilter() {
        PriceFilter priceFilter = PriceFilter.builder()
                .fromDate(LocalDate.of(2022, 7, 28).atStartOfDay()).build();
        List<GetItemPricesDto> actualResult = priceService.findAllItemPrices(
                userService.findById(USER6_ID), ITEM_CODE, priceFilter);

        assertEquals(1, actualResult.size());
        assertEquals(LocalDate.of(2022, 7, 28), actualResult.get(0).getDate().toLocalDate());
        assertEquals(1100, actualResult.get(0).getPrice());
    }

    @Test
    void findAllItemPricesWithToDateFilter() {
        PriceFilter priceFilter = PriceFilter.builder()
                .toDate(LocalDate.of(2022, 7, 27).atStartOfDay().plusDays(1L)).build();
        List<GetItemPricesDto> actualResult = priceService.findAllItemPrices(
                userService.findById(USER6_ID), ITEM_CODE, priceFilter);

        assertEquals(1, actualResult.size());
        assertEquals(LocalDate.of(2022, 7, 27), actualResult.get(0).getDate().toLocalDate());
        assertEquals(1176, actualResult.get(0).getPrice());
    }

    @Test
    void findAllItemPricesWithFromDateAndToDateFilterShouldReturnEmptyList() {
        PriceFilter priceFilter = PriceFilter.builder()
                .fromDate(LocalDate.of(2022, 6, 1).atStartOfDay())
                .toDate(LocalDate.of(2022, 6, 30).atStartOfDay()).build();
        List<GetItemPricesDto> actualResult = priceService.findAllItemPrices(
                userService.findById(USER6_ID), ITEM_CODE, priceFilter);

        assertEquals(0, actualResult.size());
    }

    @Test
    void findAllItemPricesThrowsExceptionIfItemNotFound() {
        PriceFilter priceFilter = PriceFilter.builder().build();
        RequestException requestException = assertThrows(RequestException.class,
                () -> priceService.findAllItemPrices(userService.findById(USER6_ID), ITEM_CODE_NOT_PRESENT_FOR_USER6, priceFilter));

        assertTrue(requestException.getMessage().contains(errorMsgFindPrices));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());
    }
}