package com.testproject.WbPriceTrackerApi.service;

import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.model.Price;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.repository.ItemRepository;
import com.testproject.WbPriceTrackerApi.util.ParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final ParserUtil parserUtil;

    @Autowired
    public ItemService(ItemRepository itemRepository, ParserUtil parserUtil) {
        this.itemRepository = itemRepository;
        this.parserUtil = parserUtil;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findByCode(Long code) {
        return itemRepository.findByCode(code);
    }

    @Transactional
    public void addItemToProfile(User user, Item item) {
        itemRepository.findByCode(item.getCode()).ifPresentOrElse(
//                if item with this code is present in the Db, add item only to user profile
                existingItem -> {
                    user.getItems().stream().filter(i -> i.equals(existingItem)).findAny()
                            .ifPresent(i -> {
                                log.info("Fail while adding item to profile. " +
                                        "User {} is trying to add item {} to profile for the second time", user.getUsername(), existingItem.getCode());

                                throw new RequestException("Item code " + existingItem.getCode() + " has already been added to the tracking list",
                                        HttpStatus.BAD_REQUEST);
                            });
                    user.addItem(existingItem);

                    log.info("Item {} was found in Db and added to the user profile : {}", existingItem.getCode(), user.getUsername());
                },
//                else add item to Db & user profile
                () -> {
                    addInfoFromServer(item);
                    user.addItem(item);
                    itemRepository.save(item);

                    log.info("New item {} was saved to Db and added to the user profile : {}", item.getCode(), user.getUsername());
                }
        );
    }

    @Transactional
    public void deleteItemFromProfile(User user, Long code) {
        itemRepository.findByCode(code).ifPresentOrElse(
                existingItem -> {
                    user.getItems().stream().filter(i -> i.equals(existingItem)).findAny()
                            .ifPresentOrElse(i -> {
                                        user.removeItem(existingItem);

                                        log.info("Item {} was deleted from the user profile : {}", existingItem.getCode(), user.getUsername());

                                        if (existingItem.getUsers().stream().findAny().isEmpty()) {
                                            log.info("Item {} no longer contained in any profile. Item was deleted from Db", existingItem.getCode());

                                            itemRepository.delete(existingItem);

                                        }
                                    },
                                    () -> {
                                        log.info("Fail while deleting item from the user profile : {}. " +
                                                "Item {} not found in user profile", user.getUsername(), code);

                                        throw new RequestException("Item " + code + " not found in user profile " + user.getUsername(),
                                                HttpStatus.BAD_REQUEST);
                                    }
                            );
                },
                () -> {
                    log.info("Fail while deleting item from user profile : {}. " +
                            "Item {} not found in Db", user.getUsername(), code);

                    throw new RequestException("Item " + code + " not found", HttpStatus.BAD_REQUEST);
                }
        );
    }

    private void addInfoFromServer(Item item) {
        Map<String, Object> infoFromServer = parserUtil.getInfoFromWb(item.getCode());
        item.setBrand((String) infoFromServer.get("brand"));
        item.setName((String) infoFromServer.get("name"));
        item.getPrices().add(Price.builder()
                .item(item)
                .price((Integer) infoFromServer.get("price"))
                .date(LocalDateTime.now())
                .build());
    }
}
