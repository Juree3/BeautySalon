package com.beautysalon.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingRequest {

    private Long staffId;
    private LocalDate date;
    private LocalTime startTime;
    private List<Long> serviceIds;
}