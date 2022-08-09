package com.testproject.WbPriceTrackerApi.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.testproject.WbPriceTrackerApi.dto.GetItemPricesDto;
import com.testproject.WbPriceTrackerApi.dto.PriceFilter;
import com.testproject.WbPriceTrackerApi.model.QItem;
import com.testproject.WbPriceTrackerApi.model.QPrice;
import com.testproject.WbPriceTrackerApi.util.QPredicates;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class FilterPriceRepositoryImpl implements FilterPriceRepository {

    private final EntityManager entityManager;

    @Override
    public List<GetItemPricesDto> findAllByFilter(Long id, PriceFilter priceFilter) {

        Predicate predicate = QPredicates.builder()
                .add(priceFilter.getFromDate(), QPrice.price1.date::after)
                .add(priceFilter.getToDate(), QPrice.price1.date::before)
                .build();

        JPAQuery<?> jpaQuery = new JPAQuery<>(entityManager);

        return jpaQuery
                .select(Projections.fields(GetItemPricesDto.class, QPrice.price1.price, QPrice.price1.date))
                .from(QPrice.price1)
                .join(QPrice.price1.item, QItem.item)
                .where(QItem.item.id.eq(id)
                        .and(predicate))
                .orderBy(QPrice.price1.date.desc())
                .fetch();
    }

}
