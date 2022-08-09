package com.testproject.WbPriceTrackerApi.dao;

import com.testproject.WbPriceTrackerApi.dto.GetUserItemsDto;
import com.testproject.WbPriceTrackerApi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ItemDao {

    private final JdbcTemplate jdbcTemplate;

    public List<GetUserItemsDto> findAllUsersItems(User user) {
        String sql = "SELECT DISTINCT i.code, i.brand, i.name, " +
                "(SELECT price FROM prices WHERE item_id = i.id ORDER BY date DESC LIMIT 1) " +
                "FROM users_items ui JOIN items i ON ui.item_id = i.id " +
                "JOIN users u ON ui.user_id = u.id " +
                "LEFT JOIN prices p ON i.id = p.item_id WHERE u.id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new GetUserItemsDto(
                rs.getLong("code"),
                rs.getString("brand"),
                rs.getString("name"),
                rs.getInt("price")
        ), user.getId());
    }
}
