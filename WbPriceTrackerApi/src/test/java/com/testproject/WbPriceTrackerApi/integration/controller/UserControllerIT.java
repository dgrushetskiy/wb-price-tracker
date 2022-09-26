package com.testproject.WbPriceTrackerApi.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.testproject.WbPriceTrackerApi.dto.ItemCodeDto;
import com.testproject.WbPriceTrackerApi.integration.IntegrationTestBase;
import com.testproject.WbPriceTrackerApi.service.ItemService;
import com.testproject.WbPriceTrackerApi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
class UserControllerIT extends IntegrationTestBase {

    private static final String requestMapping = "/api/v1/users";

    private static final String interceptorErrMsg = "You don't have permissions to access the resource";
    private static final String unauthorizedErrMsg = "Unauthorized: authentication required";

    //    Admin(1, 'AdminProfile', 'AdminProfile', 'admin@gmail.com','adminPassword', 'ROLE_ADMIN');
    private final static String ADMIN_USERNAME = "AdminProfile";
    //    TestUser1(6, "TestUser1", "TestUser1", "testUser1@gmail.com","password", 'ROLE_USER');
    private final static Long USER6_ID = 6L;
    private final static String USER6_USERNAME = "TestUser1";
    private final static Long USER7_ID = 7L;
    private final static Long CODE_PRESENT_FOR_USER6 = 15061497L;
    private static final Long CODE_PRESENT_ONLY_FOR_USER6 = 13458162L;
    private final static Long CODE_PRESENT_IN_DB_AND_NOT_PRESENT_FOR_USER6 = 12052239L;
    private static final Long CODE_NOT_PRESENT_IN_DB = 24874505L;

    private static final ItemCodeDto jsonDataToAddOnlyUserProfile = ItemCodeDto.builder().code(CODE_PRESENT_IN_DB_AND_NOT_PRESENT_FOR_USER6).build();
    private static final ItemCodeDto jsonDataToAddInDbAndUserProfile = ItemCodeDto.builder().code(CODE_NOT_PRESENT_IN_DB).build();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final ItemService itemService;
    private final UserService userService;

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    void getUserItems() {
        MvcResult mvcResult = mockMvc.perform(get(requestMapping + "/{userId}/items", USER6_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.username").value(USER6_USERNAME))
                .andExpect(jsonPath("$.items.length()").value(4))
                .andDo(print())
                .andReturn();

        List<Integer> codes = JsonPath.parse(mvcResult.getResponse().getContentAsString()).read("$.items[*].code");
        assertTrue(codes.contains(15061497));
        assertTrue(codes.contains(13458162));
        assertTrue(codes.contains(70456258));
        assertTrue(codes.contains(62602786));
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    @Disabled
    void getUserItemsForbiddenByInterceptor() {
        MvcResult mvcResult = mockMvc.perform(get(requestMapping + "/{userId}/items", USER7_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(interceptorErrMsg));
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    void addItemToProfileIfItemIsPresentInDb() {
        mockMvc.perform(post(requestMapping + "/{userId}/items", USER6_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonDataToAddOnlyUserProfile)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        assertTrue(itemService.findByCode(CODE_PRESENT_IN_DB_AND_NOT_PRESENT_FOR_USER6).get().getUsers()
                .contains(userService.findById(USER6_ID)));
        assertTrue(userService.findById(USER6_ID).getItems()
                .contains(itemService.findByCode(CODE_PRESENT_IN_DB_AND_NOT_PRESENT_FOR_USER6).get()));
        assertEquals(5, userService.findById(USER6_ID).getItems().size());
        assertEquals(20, itemService.findAll().size());
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    void addItemToProfileIfItemIsNotPresentInDb() {
        mockMvc.perform(post(requestMapping + "/{userId}/items", USER6_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonDataToAddInDbAndUserProfile)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        assertTrue(itemService.findByCode(CODE_NOT_PRESENT_IN_DB).get().getUsers()
                .contains(userService.findById(USER6_ID)));
        assertTrue(userService.findById(USER6_ID).getItems()
                .contains(itemService.findByCode(CODE_NOT_PRESENT_IN_DB).get()));
        assertEquals(5, userService.findById(USER6_ID).getItems().size());
        assertEquals(21, itemService.findAll().size());
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    @Disabled
    void addItemToProfileForbiddenByInterceptor() {
        MvcResult mvcResult = mockMvc.perform(post(requestMapping + "/{userId}/items", USER7_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonDataToAddInDbAndUserProfile)))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(interceptorErrMsg));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("com.testproject.WbPriceTrackerApi.integration.controller.UserControllerIT#getArgumentsForAddNotValidItemToProfileTest")
    @WithUserDetails(value = USER6_USERNAME)
    void addNotValidItemToProfile(Long code, String errMsg) {
        MvcResult mvcResult = mockMvc.perform(post(requestMapping + "/{userId}/items", USER6_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                ItemCodeDto.builder().code(code).build())))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains(errMsg));
        assertEquals(20, itemService.findAll().size());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("com.testproject.WbPriceTrackerApi.integration.controller.UserControllerIT#getArgumentsForGetItemPricesTrackingInfoWithParamsTest")
    @WithUserDetails(value = USER6_USERNAME)
    void getItemPricesTrackingInfoWithParams(String fromDate, String toDate, Integer responseSize) {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("fromDate", fromDate);
        requestParams.add("toDate", toDate);

        mockMvc.perform(get(requestMapping + "/{userId}/items/{itemCode}", USER6_ID, CODE_PRESENT_FOR_USER6)
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.code").value(CODE_PRESENT_FOR_USER6))
                .andExpect(jsonPath("$.prices.length()").value(responseSize))
                .andDo(print());
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    @Disabled
    void getItemPricesTrackingInfoForbiddenByInterceptor() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("fromDate", null);
        requestParams.add("toDate", null);

        MvcResult mvcResult = mockMvc.perform(get(requestMapping + "/{userId}/items/{itemCode}", USER7_ID, CODE_PRESENT_FOR_USER6)
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(interceptorErrMsg));
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    void deleteItemFromProfileIfItemPresentInOtherUsersLists() {
        mockMvc.perform(delete(requestMapping + "/{userId}/items/{itemCode}", USER6_ID, CODE_PRESENT_FOR_USER6))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        assertTrue(itemService.findByCode(CODE_PRESENT_FOR_USER6).isPresent());
        assertFalse(userService.findById(USER6_ID).getItems().contains(itemService.findByCode(CODE_PRESENT_FOR_USER6).get()));
        assertEquals(3, userService.findById(USER6_ID).getItems().size());
        assertEquals(20, itemService.findAll().size());
        assertFalse(itemService.findByCode(CODE_PRESENT_FOR_USER6).get().getUsers().contains(userService.findById(USER6_ID)));
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    void deleteItemFromProfileIfItemNotPresentInOtherUsersLists() {
        mockMvc.perform(delete(requestMapping + "/{userId}/items/{itemCode}", USER6_ID, CODE_PRESENT_ONLY_FOR_USER6))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
        assertTrue(itemService.findByCode(CODE_PRESENT_ONLY_FOR_USER6).isEmpty());
        assertEquals(3, userService.findById(USER6_ID).getItems().size());
        assertEquals(19, itemService.findAll().size());
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = USER6_USERNAME)
    @Disabled
    void deleteItemFromProfileForbiddenByInterceptor() {
        MvcResult mvcResult = mockMvc.perform(delete(requestMapping + "/{userId}/items/{itemCode}", USER7_ID, CODE_PRESENT_FOR_USER6))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(interceptorErrMsg));
    }

    @SneakyThrows
    @Test
    void getUserItemsUnauthorized() {
        MvcResult mvcResult = mockMvc.perform(get(requestMapping + "/{userId}/items", USER6_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(unauthorizedErrMsg));
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = ADMIN_USERNAME)
    void getUserItemsAllowedWithRoleAdmin() {
        mockMvc.perform(get(requestMapping + "/{userId}/items", USER6_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.username").value(USER6_USERNAME))
                .andExpect(jsonPath("$.items.length()").value(4))
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void addItemToProfileUnauthorized() {
        MvcResult mvcResult = mockMvc.perform(post(requestMapping + "/{userId}/items", USER6_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonDataToAddOnlyUserProfile)))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(unauthorizedErrMsg));
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = ADMIN_USERNAME)
    void addItemToProfileAllowedWithRoleAdmin() {
        mockMvc.perform(post(requestMapping + "/{userId}/items", USER6_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonDataToAddOnlyUserProfile)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void getItemPricesTrackingInfoUnauthorized() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("fromDate", null);
        requestParams.add("toDate", null);

        MvcResult mvcResult = mockMvc.perform(get(requestMapping + "/{userId}/items/{itemCode}", USER6_ID, CODE_PRESENT_FOR_USER6)
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(unauthorizedErrMsg));
    }


    @SneakyThrows
    @Test
    @WithUserDetails(value = ADMIN_USERNAME)
    void getItemPricesTrackingInfoAllowedWithRoleAdmin() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("fromDate", null);
        requestParams.add("toDate", null);

        mockMvc.perform(get(requestMapping + "/{userId}/items/{itemCode}", USER6_ID, CODE_PRESENT_FOR_USER6)
                        .params(requestParams)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @SneakyThrows
    @Test
    void deleteItemFromProfileUnauthorized() {
        MvcResult mvcResult = mockMvc.perform(delete(requestMapping + "/{userId}/items/{itemCode}", USER6_ID, CODE_PRESENT_FOR_USER6))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains(unauthorizedErrMsg));
    }

    @SneakyThrows
    @Test
    @WithUserDetails(value = ADMIN_USERNAME)
    void deleteItemFromProfileAllowedWithRoleAdmin() {
        mockMvc.perform(delete(requestMapping + "/{userId}/items/{itemCode}", USER6_ID, CODE_PRESENT_FOR_USER6))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    static Stream<Arguments> getArgumentsForAddNotValidItemToProfileTest() {
        final Long INCORRECT_CODE = 100L;
        final String errMsgForIncorrectCode = "Incorrect code. Item with this code doesn't exist";
        final Long NOT_EXISTING_CODE = 20381112L;
        final String errMsgForBrandField = "Failed to define item brand";
        final String errMsgForNameField = "Failed to define item name";
        final String errMsgForPriceField = "Failed to define item price";
        final Long CODE_WITHOUT_PRICE = 14880730L;

        return Stream.of(
                Arguments.of(INCORRECT_CODE, errMsgForIncorrectCode),
                Arguments.of(NOT_EXISTING_CODE, errMsgForBrandField),
                Arguments.of(NOT_EXISTING_CODE, errMsgForNameField),
                Arguments.of(NOT_EXISTING_CODE, errMsgForPriceField),
                Arguments.of(CODE_WITHOUT_PRICE, errMsgForPriceField)
        );
    }

    static Stream<Arguments> getArgumentsForGetItemPricesTrackingInfoWithParamsTest() {
        return Stream.of(
                Arguments.of(null, null, 2),
                Arguments.of(LocalDate.of(2022, 7, 28).toString(), null, 1),
                Arguments.of(null, LocalDate.of(2022, 7, 27).toString(), 1),
                Arguments.of(LocalDate.of(2022, 6, 1).toString(), LocalDate.of(2022, 6, 30).toString(), 0),
                Arguments.of(LocalDate.of(2022, 7, 27).toString(), LocalDate.of(2022, 7, 28).toString(), 2)
        );
    }
}
