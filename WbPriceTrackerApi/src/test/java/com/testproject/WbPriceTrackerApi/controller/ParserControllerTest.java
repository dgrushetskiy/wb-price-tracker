package com.testproject.WbPriceTrackerApi.controller;

import com.testproject.WbPriceTrackerApi.dto.ItemCodeDto;
import com.testproject.WbPriceTrackerApi.interceptor.CheckAuthInterceptor;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.security.JwtRequestAuthenticationFilter;
import com.testproject.WbPriceTrackerApi.security.JwtUtil;
import com.testproject.WbPriceTrackerApi.service.ItemService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ParserController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class) },
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class ParserControllerTest {

    private static final Long ITEM_CODE1 = 15061503L;
    private static final Long ITEM_CODE2 = 32956137L;
    private static final String ITEM_BRAND = "TestItemBrand";
    private static final String ITEM_NAME = "TestItemName";

    private static final List<Item> items = List.of(
            Item.builder().code(ITEM_CODE1).brand(ITEM_BRAND).name(ITEM_NAME).build(),
            Item.builder().code(ITEM_CODE2).brand(ITEM_BRAND).name(ITEM_NAME).build());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;
    @MockBean
    private DtoMapper dtoMapper;
    @MockBean
    private CheckAuthInterceptor checkAuthInterceptor;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private JwtRequestAuthenticationFilter jwtRequestAuthenticationFilter;

    @SneakyThrows
    @Test
    void getAllItemsCodes() {
        doReturn(items).when(itemService).findAll();
        doReturn(new ItemCodeDto(ITEM_CODE1)).when(dtoMapper).convertToItemDto(items.get(0));
        doReturn(new ItemCodeDto(ITEM_CODE2)).when(dtoMapper).convertToItemDto(items.get(1));

        mockMvc.perform(get("/api/v1/parser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.codes[0].code").value(ITEM_CODE1))
                .andExpect(jsonPath("$.codes[1].code").value(ITEM_CODE2))
                .andDo(print());
    }
}