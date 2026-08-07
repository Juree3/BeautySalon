package com.beautysalon.service;

import com.beautysalon.dto.ServiceRequest;
import com.beautysalon.dto.ServiceResponse;
import com.beautysalon.entity.Service;
import com.beautysalon.entity.User;
import com.beautysalon.enums.Role;
import com.beautysalon.repository.ServiceRepository;
import com.beautysalon.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ServiceService(ServiceRepository serviceRepository, UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }


    public ServiceResponse createService(ServiceRequest request) {

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.STAFF) {
            throw new RuntimeException("Samo staff može kreirati usluge");
        }

        Service service = new Service();

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setPrice(request.getPrice());
        service.setImageUrl(request.getImageUrl());

        service.setStaffId(currentUser.getId());
        service.setActive(true);

        return mapToResponse(serviceRepository.save(service));
    }


    public List<ServiceResponse> getAllActive() {

        return serviceRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public List<ServiceResponse> getMyServices() {

        User currentUser = getCurrentUser();

        return serviceRepository.findByStaffId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public List<ServiceResponse> getActiveByStaff(Long staffId) {

        return serviceRepository.findByStaffIdAndActiveTrue(staffId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public ServiceResponse updateService(Long id, ServiceRequest request) {

        User currentUser = getCurrentUser();

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usluga ne postoji"));


        if (!service.getStaffId().equals(currentUser.getId())) {
            throw new RuntimeException("Nije tvoja usluga");
        }


        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setPrice(request.getPrice());
        service.setImageUrl(request.getImageUrl());


        return mapToResponse(serviceRepository.save(service));
    }


    public void deleteService(Long id) {

        User currentUser = getCurrentUser();

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usluga ne postoji"));


        if (!service.getStaffId().equals(currentUser.getId())) {
            throw new RuntimeException("Nije tvoja usluga");
        }


        service.setActive(false);
        serviceRepository.save(service);
    }


    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));
    }


    private ServiceResponse mapToResponse(Service service) {

        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.getImageUrl(),
                service.getStaffId(),
                service.getActive()
        );
    }
}