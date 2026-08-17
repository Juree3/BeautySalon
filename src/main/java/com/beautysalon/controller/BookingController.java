package com.beautysalon.controller;

import com.beautysalon.dto.BookingRequest;
import com.beautysalon.dto.BookingResponse;
import com.beautysalon.entity.User;
import com.beautysalon.exception.ResourceNotFoundException;
import com.beautysalon.repository.UserRepository;
import com.beautysalon.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        BookingResponse response = bookingService.createBooking(request, customer.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {

        String email = authentication.getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        List<BookingResponse> responses = bookingService.getMyBookings(customer.getId());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<BookingResponse>> getStaffBookings(
            @RequestParam LocalDate date,
            Authentication authentication) {

        String email = authentication.getName();
        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        List<BookingResponse> responses = bookingService.getStaffBookings(staff.getId(), date);

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        BookingResponse response = bookingService.confirmBooking(id, staff.getId());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'STAFF')")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        BookingResponse response = bookingService.cancelBooking(id, user.getId(), user.getRole());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/no-show")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<BookingResponse> markNoShow(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        BookingResponse response = bookingService.markNoShow(id, staff.getId());

        return ResponseEntity.ok(response);
    }
}