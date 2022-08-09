package com.testproject.WbPriceTrackerApi.repository;

import com.testproject.WbPriceTrackerApi.model.Role;
import com.testproject.WbPriceTrackerApi.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "items")
    List<User> findByRole(Role role);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
