package com.testproject.WbPriceTrackerApi.service;

import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.repository.ItemRepository;
import com.testproject.WbPriceTrackerApi.util.ParserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    private static final Long ITEM_CODE = 15061503L;
    private static final String ITEM_BRAND = "TestItemBrand";
    private static final String ITEM_NAME = "TestItemName";
    private static final Integer ITEM_PRICE = 1000;

    private static final String addItemErrorMsg = "has already been added to the tracking list";
    private static final String deleteItemErrorMsg = "not found in user profile";
    private static final String deleteNonExistentItemErrorMsg = "not found";

    private static final Map<String, Object> infoFromServer = Map.of("brand", ITEM_BRAND,
            "name", ITEM_NAME, "price", ITEM_PRICE);

    private static final Item item = Item.builder().code(ITEM_CODE).users(new HashSet<>()).prices(new ArrayList<>()).build();
    private static final User user = User.builder().id(1L).name("TestUser1").username("TestUser1").email("testUser1@gmail.com")
            .password("password1").role(Role.ROLE_USER).items(new HashSet<>()).build();
    private static final User user2 = User.builder().id(2L).name("TestUser2").username("TestUser2").email("testUser2@gmail.com")
            .password("password2").role(Role.ROLE_USER).items(new HashSet<>()).build();

    private static final List<Item> items = List.of(item);

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ParserUtil parserUtil;
    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void prepare() {
        item.getUsers().clear();
        user.getItems().clear();
        user2.getItems().clear();
    }

    @Test
    void findAll() {
        doReturn(items).when(itemRepository).findAll();
        List<Item> actualResult = itemService.findAll();

        assertEquals(items, actualResult);
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void findByCode() {
        doReturn(Optional.of(item)).when(itemRepository).findByCode(ITEM_CODE);
        Optional<Item> actualResult = itemService.findByCode(ITEM_CODE);

        assertTrue(actualResult.isPresent());
        actualResult.ifPresent(actual -> assertEquals(item, actual));

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
    }

    @Test
    void addExistingItemToProfile() {
        doReturn(Optional.of(item)).when(itemRepository).findByCode(ITEM_CODE);
        itemService.addItemToProfile(user, item);

        assertTrue(user.getItems().contains(item));
        assertTrue(item.getUsers().contains(user));

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
    }

    @Test
    @Disabled
    void addExistingItemToProfileThrowsExceptionIfItemIsAlreadyOnTrackingList() {
        doReturn(Optional.of(item)).when(itemRepository).findByCode(ITEM_CODE);
        user.addItem(item);

        RequestException requestException = assertThrows(RequestException.class,
                () -> itemService.addItemToProfile(user, item));

        assertTrue(requestException.getMessage().endsWith(addItemErrorMsg));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
    }

    @Test
    void addNonExistentItemToDbAndProfile() {
        doReturn(Optional.empty()).when(itemRepository).findByCode(ITEM_CODE);
        doReturn(infoFromServer).when(parserUtil).getInfoFromWb(ITEM_CODE);
        itemService.addItemToProfile(user, item);

        assertTrue(user.getItems().contains(item));
        assertTrue(item.getUsers().contains(user));
        assertEquals(ITEM_BRAND, item.getBrand());
        assertEquals(ITEM_NAME, item.getName());

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void deleteItemFromProfileIfItemPresentInOtherUsersLists() {
        doReturn(Optional.of(item)).when(itemRepository).findByCode(ITEM_CODE);
        user.addItem(item);
        user2.addItem(item);
        itemService.deleteItemFromProfile(user, item.getCode());

        assertEquals(1, item.getUsers().size());
        assertFalse(item.getUsers().contains(user));
        assertFalse(user.getItems().contains(item));
        assertTrue(item.getUsers().contains(user2));
        assertTrue(user2.getItems().contains(item));

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
        verify(itemRepository, times(0)).delete(item);
    }

    @Test
    void deleteItemFromProfileIfItemNotPresentInOtherUsersLists() {
        doReturn(Optional.of(item)).when(itemRepository).findByCode(ITEM_CODE);
        user.addItem(item);
        itemService.deleteItemFromProfile(user, item.getCode());

        assertEquals(0, item.getUsers().size());
        assertFalse(item.getUsers().contains(user));
        assertFalse(user.getItems().contains(item));

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    @Disabled
    void deleteItemFromProfileThrowsExceptionIfItemNotPresentInProfile() {
        doReturn(Optional.of(item)).when(itemRepository).findByCode(ITEM_CODE);

        RequestException requestException = assertThrows(RequestException.class,
                () -> itemService.deleteItemFromProfile(user, item.getCode()));

        assertTrue(requestException.getMessage().contains(deleteItemErrorMsg));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
        verify(itemRepository, times(0)).delete(item);
    }

    @Test
    @Disabled
    void deleteItemFromProfileThrowsExceptionIfItemDoesntExist() {
        doReturn(Optional.empty()).when(itemRepository).findByCode(ITEM_CODE);

        RequestException requestException = assertThrows(RequestException.class,
                () -> itemService.deleteItemFromProfile(user, item.getCode()));

        assertTrue(requestException.getMessage().endsWith(deleteNonExistentItemErrorMsg));
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getStatus());

        verify(itemRepository, times(1)).findByCode(ITEM_CODE);
        verify(itemRepository, times(0)).delete(item);
    }
}
