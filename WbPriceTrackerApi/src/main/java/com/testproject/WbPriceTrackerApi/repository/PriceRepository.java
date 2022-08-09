package com.testproject.WbPriceTrackerApi.repository;

import com.testproject.WbPriceTrackerApi.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long>,
        FilterPriceRepository,
        QuerydslPredicateExecutor<Price> {

}
