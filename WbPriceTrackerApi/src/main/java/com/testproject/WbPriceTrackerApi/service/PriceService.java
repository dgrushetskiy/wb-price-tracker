package com.testproject.WbPriceTrackerApi.service;

import com.testproject.WbPriceTrackerApi.dto.GetItemPricesDto;
import com.testproject.WbPriceTrackerApi.dto.PriceFilter;
import com.testproject.WbPriceTrackerApi.exception.RequestException;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.model.Price;
import com.testproject.WbPriceTrackerApi.model.User;
import com.testproject.WbPriceTrackerApi.repository.ItemRepository;
import com.testproject.WbPriceTrackerApi.repository.PriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PriceService {

    private final PriceRepository priceRepository;
    private final ItemRepository itemRepository;

    public PriceService(PriceRepository priceRepository, ItemRepository itemRepository) {
        this.priceRepository = priceRepository;
        this.itemRepository = itemRepository;
    }

    public List<GetItemPricesDto> findAllItemPrices(User user, Long code, PriceFilter priceFilter) {
        Optional<Item> optionalItem = user.getItems().stream().filter(i -> i.getCode().equals(code)).findAny();
        if (optionalItem.isEmpty()) {
            log.info("Fail while getting item prices from the user profile : {}. " +
                    "Item {} not found in user profile", user.getUsername(), code);

            throw new RequestException("Item " + code + " not found in user profile " + user.getUsername(),
                    HttpStatus.BAD_REQUEST);
        }
        return priceRepository.findAllByFilter(optionalItem.get().getId(), priceFilter);
    }

    @Transactional
    public void addPriceFromParser(Price price) {
        itemRepository.findByCode(price.getItem().getCode()).ifPresentOrElse(
                existingItem -> {
                    price.setItem(existingItem);
                    existingItem.getPrices().add(price);
                    priceRepository.save(price);
                },
                () -> {
                    log.warn("Fail while adding updated price from parser. Item with code {} wasn't found", price.getItem().getCode());

                    throw new RequestException("Fail while adding updated price from parser. Item with code " + price.getItem().getCode() + " wasn't found",
                            HttpStatus.BAD_REQUEST);
                }
        );
    }
}
