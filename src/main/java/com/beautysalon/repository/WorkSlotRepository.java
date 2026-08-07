package com.beautysalon.repository;

import com.beautysalon.entity.WorkSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkSlotRepository extends JpaRepository<WorkSlot,Long> {
    List<WorkSlot> findByStaffIdAndDate(Long staffId, LocalDate date);
    List<WorkSlot> findByStaffId(Long staffId);
    List<WorkSlot> findByStaffIdAndDateBetween(Long staffId, LocalDate from, LocalDate to);
}
