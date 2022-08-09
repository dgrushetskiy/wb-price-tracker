package com.testproject.WbPriceTrackerApi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "prices")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @NotNull(message = "The field must not be empty")
    private Item item;

    @Column(name = "price")
    @NotNull(message = "The field must not be empty")
    private Integer price;

    @Column(name = "date")
    @NotNull(message = "The field must not be empty")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public Price(Item item, Integer price, LocalDateTime date) {
        this.item = item;
        this.price = price;
        this.date = date;
    }
}
