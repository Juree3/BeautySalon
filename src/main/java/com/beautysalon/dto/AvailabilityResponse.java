package com.beautysalon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AvailabilityResponse {
    private boolean scheduleKnown;
    private List<LocalTime> slots;
}