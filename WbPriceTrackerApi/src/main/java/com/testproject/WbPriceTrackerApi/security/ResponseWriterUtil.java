package com.testproject.WbPriceTrackerApi.security;

import com.testproject.WbPriceTrackerApi.exception.MessageConstant;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseWriterUtil {

    @SneakyThrows
    public static void writeResponse(HttpServletResponse response, String message, Integer status) {

        JSONObject json = new JSONObject();
        json.put("message", message);
        json.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(MessageConstant.DATE_TIME_PATTERN)));

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        response.getWriter().write(json.toString());
    }
}
