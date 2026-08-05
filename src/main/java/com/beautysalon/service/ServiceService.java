package com.beautysalon.service;

import com.beautysalon.dto.ServiceRequest;
import com.beautysalon.dto.ServiceResponse;
import com.beautysalon.entity.Service;
import com.beautysalon.entity.User;
import com.beautysalon.repository.ServiceRepository;
import com.beautysalon.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service

public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ServiceService(ServiceRepository serviceRepository, UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }


    public ServiceResponse createService(ServiceRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setPrice(request.getPrice());
        service.setImageUrl(request.getImageUrl());
        service.setStaffId(currentUser.getId());
        service.setActive(true);

        Service saved = serviceRepository.save(service);

        return new ServiceResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getDurationMinutes(),
                saved.getPrice(),
                saved.getImageUrl(),
                saved.getStaffId(),
                saved.getActive()
        );
    }

    public List<ServiceResponse> getAllActive() {

        return serviceRepository.findByActiveTrue()
                .stream()
                .map(service -> new ServiceResponse(
                        service.getId(),
                        service.getName(),
                        service.getDescription(),
                        service.getDurationMinutes(),
                        service.getPrice(),
                        service.getImageUrl(),
                        service.getStaffId(),
                        service.getActive()

                ))
                .toList();
    }
    public List<ServiceResponse> getMyServices() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));
            return serviceRepository.findByStaffId(currentUser.getId())
                    .stream().map(service -> new ServiceResponse(
                            service.getId(),
                            service.getName(),
                            service.getDescription(),
                            service.getDurationMinutes(),
                            service.getPrice(),
                            service.getImageUrl(),
                            service.getStaffId(),
                            service.getActive()
                    )).toList();
    }
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));
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

        Service saved = serviceRepository.save(service);

        return new ServiceResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getDurationMinutes(),
                saved.getPrice(),
                saved.getImageUrl(),
                saved.getStaffId(),
                saved.getActive()
        );
    }

    public void deleteService(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usluga ne postoji"));
        if (!service.getStaffId().equals(currentUser.getId())) {
            throw new RuntimeException("Nije tvoja usluga");
        }
        service.setActive(false);
        serviceRepository.save(service);
    }
}
