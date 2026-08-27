package com.beautysalon.controller;

import com.beautysalon.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer durationMinutes) {

        List<LocalTime> slots = availabilityService.getAvailableSlots(staffId, date, durationMinutes);

        return ResponseEntity.ok(slots);
    }
}