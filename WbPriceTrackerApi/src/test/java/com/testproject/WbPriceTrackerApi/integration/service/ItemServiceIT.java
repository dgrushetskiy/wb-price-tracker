package com.testproject.WbPriceTrackerApi.integration.service;

import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.integration.IntegrationTestBase;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.repository.ItemRepository;
import com.testproject.WbPriceTrackerApi.service.ItemService;
import com.testproject.WbPriceTrackerApi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ItemServiceIT extends IntegrationTestBase {

    private static final Long USER6_ID = 6L;

    private static final Long CODE_PRESENT_FOR_USER6 = 15061497L;
    private static final Long CODE_PRESENT_ONLY_FOR_USER6 = 13458162L;
    private static final Long CODE_NOT_PRESENT_FOR_USER6 = 12052239L;
    private static final Long CODE_NOT_PRESENT_IN_DB = 24874505L;

    private static final String addItemErrorMsg = "has already been added to the tracking list";
    private static final String deleteItemErrorMsg = "not found in user profile";
    private static final String deleteNonExistentItemErrorMsg = "not found";

    private final ItemService itemService;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Test
    void findAll() {
        List<Item> actualResult = itemService.findAll();
        assertEquals(20, actualResult.size());
        assertEquals(15061497L, actualResult.get(10).getCode());
    }

    @Test
    void findOptionalItemByCode() {
        Optional<Item> actualResultPresent = itemService.findByCode(CODE_PRESENT_FOR_USER6);
        Optional<Item> actualResultNotPresent = itemService.findByCode(CODE_NOT_PRESENT_IN_DB);

        assertTrue(actualResultPresent.isPresent());
        assertEquals("Ticle", actualResultPresent.get().getBrand());
        assertTrue(actualResultNotPresent.isEmpty());
    }

    @Test
    void addExistingItemToProfile() {
        itemService.addItemToProfile(userService.findById(USER6_ID), Item.builder().code(CODE_NOT_PRESENT_FOR_USER6).build());

        assertTrue(userService.findById(USER6_ID).getItems().contains(itemRepository.findByCode(CODE_NOT_PRESENT_FOR_USER6).get()));
        assertEquals(5, userService.findById(USER6_ID).getItems().size());
        assertEquals(20, itemService.findAll().size());
        assertTrue(itemRepository.findByCode(CODE_NOT_PRESENT_FOR_USER6).get().getUsers().contains(userService.findById(USER6_ID)));
    }

    @Test
    void addExistingItemToProfileThrowsExceptionIfItemIsAlreadyOnTrackingList() {
        RequestException requestException = assertThrows(RequestException.class,
                () -> itemService.addItemToProfile(userService.findById(USER6_ID),
                        Item.builder().code(CODE_PRESENT_FOR_USER6).build()));

        assertTrue(requestException.getMessage().endsWith(addItemErrorMsg));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());
    }

    @Test
    void addNonExistentItemToDbAndProfile() {
        itemService.addItemToProfile(userService.findById(USER6_ID),
                Item.builder().code(CODE_NOT_PRESENT_IN_DB).users(new HashSet<>()).prices(new ArrayList<>()).build());

        assertTrue(userService.findById(USER6_ID).getItems().contains(itemRepository.findByCode(CODE_NOT_PRESENT_IN_DB).get()));
        assertEquals(5, userService.findById(USER6_ID).getItems().size());
        assertEquals(21, itemService.findAll().size());
        assertTrue(itemRepository.findByCode(CODE_NOT_PRESENT_IN_DB).get().getUsers().contains(userService.findById(USER6_ID)));
    }

    @Test
    void deleteItemFromProfileIfItemPresentInOtherUsersLists() {
        itemService.deleteItemFromProfile(userService.findById(USER6_ID), CODE_PRESENT_FOR_USER6);

        assertTrue(itemRepository.findByCode(CODE_PRESENT_FOR_USER6).isPresent());
        assertFalse(userService.findById(USER6_ID).getItems().contains(itemRepository.findByCode(CODE_PRESENT_FOR_USER6).get()));
        assertEquals(3, userService.findById(USER6_ID).getItems().size());
        assertEquals(20, itemService.findAll().size());
        assertFalse(itemRepository.findByCode(CODE_PRESENT_FOR_USER6).get().getUsers().contains(userService.findById(USER6_ID)));
    }

    @Test
    void deleteItemFromProfileIfItemNotPresentInOtherUsersLists() {
        itemService.deleteItemFromProfile(userService.findById(USER6_ID), CODE_PRESENT_ONLY_FOR_USER6);

        assertTrue(itemRepository.findByCode(CODE_PRESENT_ONLY_FOR_USER6).isEmpty());
        assertEquals(3, userService.findById(USER6_ID).getItems().size());
        assertEquals(19, itemService.findAll().size());
    }

    @Test
    void deleteItemFromProfileThrowsExceptionIfItemNotPresentInProfile() {
        RequestException requestException = assertThrows(RequestException.class,
                () -> itemService.deleteItemFromProfile(userService.findById(USER6_ID), CODE_NOT_PRESENT_FOR_USER6));

        assertTrue(requestException.getMessage().contains(deleteItemErrorMsg));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());
    }

    @Test
    void deleteItemFromProfileThrowsExceptionIfItemDoesntExist() {
        RequestException requestException = assertThrows(RequestException.class,
                () -> itemService.deleteItemFromProfile(userService.findById(USER6_ID), CODE_NOT_PRESENT_IN_DB));

        assertTrue(requestException.getMessage().endsWith(deleteNonExistentItemErrorMsg));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());
    }
}