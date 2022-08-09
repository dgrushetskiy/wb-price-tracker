package com.testproject.WbPriceTrackerApi.repository;

import com.testproject.WbPriceTrackerApi.dto.GetItemPricesDto;
import com.testproject.WbPriceTrackerApi.dto.PriceFilter;

import java.util.List;

public interface FilterPriceRepository {

    List<GetItemPricesDto> findAllByFilter(Long id, PriceFilter priceFilter);
}
