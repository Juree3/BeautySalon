package com.beautysalon.repository;

import com.beautysalon.entity.Booking;
import com.beautysalon.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStaffIdAndDateAndStatusIn(
            Long staffId, LocalDate date, List<BookingStatus> statuses);

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByStaffIdAndDate(Long staffId, LocalDate date);
}