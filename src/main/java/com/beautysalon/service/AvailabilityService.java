package com.beautysalon.service;

import com.beautysalon.dto.AvailabilityResponse;
import com.beautysalon.entity.Booking;
import com.beautysalon.entity.WorkSlot;
import com.beautysalon.enums.BookingStatus;
import com.beautysalon.repository.BookingRepository;
import com.beautysalon.repository.WorkSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityService {

    private final BookingRepository bookingRepository;
    private final WorkSlotRepository workSlotRepository;

    public AvailabilityService(BookingRepository bookingRepository,
                               WorkSlotRepository workSlotRepository) {
        this.bookingRepository = bookingRepository;
        this.workSlotRepository = workSlotRepository;
    }

    public AvailabilityResponse getAvailableSlots(Long staffId, LocalDate date, Integer durationMinutes) {

        List<WorkSlot> workSlots = workSlotRepository.findByStaffIdAndDate(staffId, date);

        if (workSlots.isEmpty()) {
            return new AvailabilityResponse(false, new ArrayList<>());
        }

        List<Booking> existingBookings = bookingRepository.findByStaffIdAndDateAndStatusIn(
                staffId, date, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));

        List<LocalTime> availableSlots = new ArrayList<>();

        for (int j = 0; j < workSlots.size(); j++) {

            WorkSlot slot = workSlots.get(j);

            LocalTime candidate = slot.getStartTime();

            while (!candidate.plusMinutes(durationMinutes).isAfter(slot.getEndTime())) {

                LocalTime candidateEnd = candidate.plusMinutes(durationMinutes);
                boolean isAvailable = true;

                for (int k = 0; k < existingBookings.size(); k++) {
                    Booking existing = existingBookings.get(k);

                    boolean overlaps = candidate.isBefore(existing.getEndTime())
                            && candidateEnd.isAfter(existing.getStartTime());

                    if (overlaps) {
                        isAvailable = false;
                    }
                }

                boolean isInPast = date.equals(LocalDate.now()) && candidate.isBefore(LocalTime.now());

                if (isAvailable && !isInPast) {
                    availableSlots.add(candidate);
                }

                candidate = candidate.plusMinutes(15);
            }
        }

        return new AvailabilityResponse(true, availableSlots);
    }
}