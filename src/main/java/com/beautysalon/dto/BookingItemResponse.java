package com.beautysalon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BookingItemResponse {

    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
    private Integer durationMinutes;
}