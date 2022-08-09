package com.testproject.WbPriceTrackerApi.service;

import com.testproject.WbPriceTrackerApi.dto.GetItemPricesDto;
import com.testproject.WbPriceTrackerApi.dto.PriceFilter;
import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.model.Price;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.repository.ItemRepository;
import com.testproject.WbPriceTrackerApi.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    private static final Long ITEM_CODE = 15061503L;
    private static final Integer ITEM_PRICE = 1000;
    private static final LocalDate ITEM_DATE = LocalDate.of(2022, 7, 1);
    private static final LocalTime ITEM_TIME = LocalTime.of(10, 0, 0);

    private static final User user = User.builder().id(1L).name("TestUser1").username("TestUser1").email("testUser1@gmail.com")
            .password("password1").role(Role.ROLE_USER).items(new HashSet<>()).build();
    private static final Item item = Item.builder().id(1L).code(ITEM_CODE).users(new HashSet<>()).prices(new ArrayList<>()).build();
    private static final Price price = Price.builder()
            .item(Item.builder().code(ITEM_CODE).prices(new ArrayList<>()).build())
            .price(ITEM_PRICE)
            .date(LocalDateTime.of(ITEM_DATE, ITEM_TIME))
            .build();

    private static final PriceFilter priceFilter = PriceFilter.builder().build();
    private static final List<GetItemPricesDto> prices = List.of(GetItemPricesDto.builder()
            .price(ITEM_PRICE)
            .date(LocalDateTime.of(ITEM_DATE, ITEM_TIME))
            .build());

    private static final String errorMsgAddPrice = "Fail while adding updated price from parser";
    private static final String errorMsgFindPrices = "not found in user profile";

    @Mock
    private PriceRepository priceRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private PriceService priceService;

    @BeforeEach
    void prepare() {
        user.getItems().clear();
    }

    @Test
    void addPriceFromParser() {
        doReturn(Optional.of(item)).when(itemRepository).findByCode(ITEM_CODE);
        priceService.addPriceFromParser(price);

        assertEquals(item, price.getItem());
        assertTrue(item.getPrices().contains(price));
        assertEquals(ITEM_PRICE, item.getPrices().get(0).getPrice());

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
        verify(priceRepository, times(1)).save(price);
    }

    @Test
    void addPriceFromParserThrowsExceptionIfItemNotFound() {
        doReturn(Optional.empty()).when(itemRepository).findByCode(ITEM_CODE);
        RequestException requestException = assertThrows(RequestException.class, () -> priceService.addPriceFromParser(price));

        assertTrue(requestException.getMessage().startsWith(errorMsgAddPrice));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
        verifyNoInteractions(priceRepository);
    }

    @Test
    void findAllItemPrices() {
        user.getItems().add(item);
        doReturn(prices).when(priceRepository).findAllByFilter(item.getId(), priceFilter);
        List<GetItemPricesDto> actualResult = priceService.findAllItemPrices(user, ITEM_CODE, priceFilter);

        assertTrue(user.getItems().contains(item));
        assertEquals(ITEM_PRICE, actualResult.get(0).getPrice());

        verify(priceRepository, times(1)).findAllByFilter(item.getId(), priceFilter);
    }

    @Test
    void findAllItemPricesThrowsExceptionIfItemNotFound() {
        RequestException requestException = assertThrows(RequestException.class, () -> priceService.findAllItemPrices(user, ITEM_CODE, priceFilter));

        assertTrue(requestException.getMessage().contains(errorMsgFindPrices));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());

        verifyNoInteractions(priceRepository);
    }
}