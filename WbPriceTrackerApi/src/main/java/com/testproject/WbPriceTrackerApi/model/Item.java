package com.testproject.WbPriceTrackerApi.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    @NotNull(message = "{field.notNull}")
    @Min(value = 2_000_000L, message = "{incorrect.code}")
    private Long code;

    @Column(name = "brand")
    @NotNull(message = "{field.notNull}")
    private String brand;

    @Column(name = "name")
    @NotNull(message = "{field.notNull}")
    private String name;

    @ManyToMany(mappedBy = "items")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Price> prices = new ArrayList<>();
}
