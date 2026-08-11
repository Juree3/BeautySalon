package com.beautysalon.dto;

import com.beautysalon.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long staffId;
    private String staffName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private Integer totalDurationMinutes;
    private LocalDateTime createdAt;
    private List<BookingItemResponse> items;
}