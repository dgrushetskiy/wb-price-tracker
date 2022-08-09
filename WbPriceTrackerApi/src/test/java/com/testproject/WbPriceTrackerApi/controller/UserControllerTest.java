package com.testproject.WbPriceTrackerApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testproject.WbPriceTrackerApi.dao.ItemDao;
import com.testproject.WbPriceTrackerApi.dto.GetItemPricesDto;
import com.testproject.WbPriceTrackerApi.dto.GetUserItemsDto;
import com.testproject.WbPriceTrackerApi.dto.ItemCodeDto;
import com.testproject.WbPriceTrackerApi.dto.PriceFilter;
import com.testproject.WbPriceTrackerApi.interceptor.CheckAuthInterceptor;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.security.JwtRequestAuthenticationFilter;
import com.testproject.WbPriceTrackerApi.security.JwtUtil;
import com.testproject.WbPriceTrackerApi.service.ItemService;
import com.testproject.WbPriceTrackerApi.service.PriceService;
import com.testproject.WbPriceTrackerApi.service.UserService;
import com.testproject.WbPriceTrackerApi.util.DtoMapper;
import com.testproject.WbPriceTrackerApi.validator.ItemValidator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private static final String requestMapping = "/api/v1/users";

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "TestUser1";
    private static final User user1 = User.builder().id(USER_ID).name(USERNAME).username(USERNAME).email("testUser1@gmail.com")
            .password("password").role(Role.ROLE_USER).build();

    private static final Long ITEM_CODE1 = 15061503L;
    private static final Long ITEM_CODE2 = 32956137L;
    private static final List<GetUserItemsDto> items = List.of(
            GetUserItemsDto.builder().code(ITEM_CODE1).brand("TestItemBrand1").name("TestItemName1").price(1000).build(),
            GetUserItemsDto.builder().code(ITEM_CODE2).brand("TestItemBrand2").name("TestItemName1").price(500).build());

    private static final ItemCodeDto jsonDataToAdd = ItemCodeDto.builder().code(ITEM_CODE1).build();
    private static final Item item = Item.builder().code(ITEM_CODE1).build();
    private static final List<GetItemPricesDto> allItemPrices = List.of(
            GetItemPricesDto.builder().price(1000).date(LocalDateTime.of(2022, 8, 1, 12, 0)).build(),
            GetItemPricesDto.builder().price(500).date(LocalDateTime.of(2022, 8, 2, 12, 0)).build());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private PriceService priceService;
    @MockBean
    private ItemDao itemDao;
    @MockBean
    private ItemService itemService;
    @MockBean
    private DtoMapper dtoMapper;
    @MockBean
    private ItemValidator itemValidator;
    @MockBean
    CheckAuthInterceptor checkAuthInterceptor;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private JwtRequestAuthenticationFilter jwtRequestAuthenticationFilter;

    @Captor
    ArgumentCaptor<PriceFilter> priceFilterCaptor;

    @SneakyThrows
    @BeforeEach
    void prepare() {
        doReturn(true).when(checkAuthInterceptor).preHandle(any(), any(), any());
    }

    @SneakyThrows
    @Test
    void getUserItems() {
        doReturn(user1).when(userService).findById(USER_ID);
        doReturn(items).when(itemDao).findAllUsersItems(user1);

        mockMvc.perform(get(requestMapping + "/{userId}/items", USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.items[0].code").value(ITEM_CODE1))
                .andExpect(jsonPath("$.items[1].code").value(ITEM_CODE2))
                .andDo(print());
        verify(userService, times(1)).findById(USER_ID);
        verify(itemDao, times(1)).findAllUsersItems(user1);
    }

    @SneakyThrows
    @Test
    void addItemToProfileIfItemIsPresentInDb() {
        doReturn(item).when(dtoMapper).convertToItem(any(ItemCodeDto.class));
        doReturn(Optional.of(item)).when(itemService).findByCode(ITEM_CODE1);
        doReturn(user1).when(userService).findById(USER_ID);

        mockMvc.perform(post(requestMapping + "/{userId}/items", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonDataToAdd)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        verify(dtoMapper, times(1)).convertToItem(any(ItemCodeDto.class));
        verify(itemService, times(1)).findByCode(ITEM_CODE1);
        verify(userService, times(1)).findById(USER_ID);
        verify(itemService, times(1)).addItemToProfile(user1, item);
        verifyNoInteractions(itemValidator);
    }

    @SneakyThrows
    @Test
    void addItemToProfileIfItemIsNotPresentInDb() {
        doReturn(item).when(dtoMapper).convertToItem(any(ItemCodeDto.class));
        doReturn(Optional.empty()).when(itemService).findByCode(ITEM_CODE1);
        doReturn(user1).when(userService).findById(USER_ID);

        mockMvc.perform(post(requestMapping + "/{userId}/items", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonDataToAdd)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        verify(dtoMapper, times(1)).convertToItem(any(ItemCodeDto.class));
        verify(itemService, times(1)).findByCode(ITEM_CODE1);
        verify(userService, times(1)).findById(USER_ID);
        verify(itemService, times(1)).addItemToProfile(user1, item);
        verify(itemValidator, times(1)).validate(any(Item.class), any(BindingResult.class));
    }

    @SneakyThrows
    @Test
    void getItemPricesTrackingInfoWithoutParams() {
        doReturn(user1).when(userService).findById(USER_ID);
        doReturn(allItemPrices).when(priceService).findAllItemPrices(any(User.class), anyLong(), any(PriceFilter.class));

        mockMvc.perform(get(requestMapping + "/{userId}/items/{itemCode}", USER_ID, ITEM_CODE1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.code").value(ITEM_CODE1))
                .andExpect(jsonPath("$.prices[0].price").value(1000))
                .andExpect(jsonPath("$.prices[1].price").value(500))
                .andDo(print());
        verify(userService, times(1)).findById(USER_ID);
        verify(priceService, times(1)).findAllItemPrices(any(User.class), anyLong(), priceFilterCaptor.capture());
        assertNull(priceFilterCaptor.getValue().getFromDate());
        assertNull(priceFilterCaptor.getValue().getFromDate());
    }

    @SneakyThrows
    @Test
    void getItemPricesTrackingInfoWithParams() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        String fromDate = "2022-07-01"; String toDate = "2022-09-01";
        requestParams.add("fromDate", fromDate);
        requestParams.add("toDate", toDate);

        doReturn(user1).when(userService).findById(USER_ID);
        doReturn(allItemPrices).when(priceService).findAllItemPrices(any(User.class), anyLong(), any(PriceFilter.class));

        mockMvc.perform(get(requestMapping + "/{userId}/items/{itemCode}", USER_ID, ITEM_CODE1)
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.code").value(ITEM_CODE1))
                .andExpect(jsonPath("$.prices[0].price").value(1000))
                .andExpect(jsonPath("$.prices[1].price").value(500))
                .andDo(print());
        verify(userService, times(1)).findById(USER_ID);
        verify(priceService, times(1)).findAllItemPrices(any(User.class), anyLong(), priceFilterCaptor.capture());
        assertEquals(LocalDate.parse(fromDate).atStartOfDay(), priceFilterCaptor.getValue().getFromDate());
        assertEquals(LocalDate.parse(toDate).atStartOfDay().plusDays(1L), priceFilterCaptor.getValue().getToDate());
    }

    @SneakyThrows
    @Test
    void deleteItemFromProfile() {
        doReturn(user1).when(userService).findById(USER_ID);
        mockMvc.perform(delete(requestMapping + "/{userId}/items/{itemCode}", USER_ID, ITEM_CODE1))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        verify(userService, times(1)).findById(USER_ID);
        verify(itemService, times(1)).deleteItemFromProfile(user1, ITEM_CODE1);
    }
}