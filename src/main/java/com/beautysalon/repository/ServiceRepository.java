package com.beautysalon.repository;

import com.beautysalon.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service,Long> {
    List<Service> findByActiveTrue();
    List<Service> findByStaffId(Long staffId);
}
