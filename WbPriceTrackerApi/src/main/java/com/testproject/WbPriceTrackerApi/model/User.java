package com.testproject.WbPriceTrackerApi.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "The field must not be empty")
    @Size(min = 2, max = 128, message = "The field must contain from 2 to 128 characters")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "The field must not be empty")
    @Size(min = 2, max = 128, message = "The field must contain from 2 to 128 characters")
    @Column(name = "username")
    private String username;

    @Pattern(regexp = "^.+@.+(\\.[^.]+)+$", message = "Incorrect email. Email pattern : xx@xx.xx")
    private String email;

    @NotEmpty(message = "The field must not be empty")
    @Size(min = 5, message = "Password must contain at least 5 characters")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "users_items",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private Set<Item> items = new HashSet<>();

    public void addItem(Item item) {
        this.items.add(item);
        item.getUsers().add(this);
    }

    public void removeItem(Item item) {
        this.items.remove(item);
        item.getUsers().remove(this);
    }
}
