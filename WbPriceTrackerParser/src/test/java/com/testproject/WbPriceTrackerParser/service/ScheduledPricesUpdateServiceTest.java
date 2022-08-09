package com.testproject.WbPriceTrackerParser.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledPricesUpdateServiceTest {

    private static final Long ITEM_CODE1 = 15061503L;
    private static final Long ITEM_CODE2 = 32956137L;
    private static final List<Long> codesFromApp = List.of(ITEM_CODE1, ITEM_CODE2);

    @Mock
    private ParserService parserService;
    @Mock
    private AsyncPriceUpdateService asyncPriceUpdateService;
    @InjectMocks
    private ScheduledPricesUpdateService scheduledPricesUpdateService;

    @SneakyThrows
    @Test
    void updateItemsPrices() {
        doReturn(codesFromApp).when(parserService).getListOfItemsCodesFromApp();
        scheduledPricesUpdateService.updateItemsPrices();

        verify(parserService, times(1)).getListOfItemsCodesFromApp();
        verify(asyncPriceUpdateService, times(2)).updatePrice(anyLong());
    }

    @Test
    void updateItemsPricesIfCodesFromAppIsEmpty() {
        doReturn(List.of()).when(parserService).getListOfItemsCodesFromApp();

        scheduledPricesUpdateService.updateItemsPrices();

        verify(parserService, times(1)).getListOfItemsCodesFromApp();
        verify(asyncPriceUpdateService, times(0)).updatePrice(anyLong());
    }
}