package com.testproject.WbPriceTrackerApi.util;

import com.testproject.WbPriceTrackerApi.dto.ItemCodeDto;
import com.testproject.WbPriceTrackerApi.dto.PriceDto;
import com.testproject.WbPriceTrackerApi.dto.RegisterDto;
import com.testproject.WbPriceTrackerApi.dto.UserForAdminDto;
import com.testproject.WbPriceTrackerApi.model.Item;
import com.testproject.WbPriceTrackerApi.model.Price;
import com.testproject.WbPriceTrackerApi.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DtoMapper {

    private final ModelMapper modelMapper;

    public User convertToUser(RegisterDto registerDto) {
        return modelMapper.map(registerDto, User.class);
    }

    public UserForAdminDto convertToUserDto(User user) {
        return modelMapper.map(user, UserForAdminDto.class);
    }

    public Item convertToItem(ItemCodeDto itemCodeDto) {
        return modelMapper.map(itemCodeDto, Item.class);
    }

    public ItemCodeDto convertToItemDto(Item item) {
        return modelMapper.map(item, ItemCodeDto.class);
    }

    public Price convertToPrice(PriceDto priceDto) {
        return modelMapper.map(priceDto, Price.class);
    }

}
