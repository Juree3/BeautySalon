package com.beautysalon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class WorkSlotResponse {

    private Long id;
    private Long staffId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
