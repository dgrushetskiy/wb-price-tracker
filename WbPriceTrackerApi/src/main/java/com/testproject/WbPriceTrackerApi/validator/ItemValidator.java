package com.testproject.WbPriceTrackerApi.validator;

import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.util.ParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class ItemValidator implements Validator {

    private final ParserUtil parserUtil;

    @Autowired
    public ItemValidator(ParserUtil parserUtil) {
        this.parserUtil = parserUtil;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Item.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;
        String json = parserUtil.getJson(item.getCode());
        if (parserUtil.getBrandFromWb(json) == null) {
            log.warn("Add Item In Profile Method Called. Fail while parsing JSON {}. Failed to get item {} brand.",
                    json, item.getCode());

            errors.reject("", "Failed to define item brand for code " + item.getCode());

        }
        if (parserUtil.getNameFromWb(json) == null) {
            log.warn("Add Item In Profile Method Called. Fail while parsing JSON {}. Failed to get item {} name.",
                    json, item.getCode());

            errors.reject("", "Failed to define item name for code " + item.getCode());
        }
        if (parserUtil.getPriceFromWb(json) == null) {
            log.warn("Add Item In Profile Method Called. Fail while parsing JSON {}. Failed to get item {} price.",
                    json, item.getCode());

            errors.reject("", "Failed to define item price for code " + item.getCode());
        }
    }
}
