package com.beautysalon.controller;

import com.beautysalon.dto.ServiceRequest;
import com.beautysalon.dto.ServiceResponse;
import com.beautysalon.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STAFF')")
    public ServiceResponse create(@Valid @RequestBody ServiceRequest request) {
        return serviceService.createService(request);
    }

    @GetMapping
    public List<ServiceResponse> getAllActive() {
        return serviceService.getAllActive();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STAFF')")
    public List<ServiceResponse> getMyServices() {
        return serviceService.getMyServices();
    }

    @GetMapping("/staff/{staffId}")
    public List<ServiceResponse> getByStaff(@PathVariable Long staffId) {
        return serviceService.getActiveByStaff(staffId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ServiceResponse update(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        return serviceService.updateService(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('STAFF')")
    public void delete(@PathVariable Long id) {
        serviceService.deleteService(id);
    }
}