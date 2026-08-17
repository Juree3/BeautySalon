package com.beautysalon.service;

import com.beautysalon.dto.BookingItemResponse;
import com.beautysalon.dto.BookingRequest;
import com.beautysalon.dto.BookingResponse;
import com.beautysalon.entity.Booking;
import com.beautysalon.entity.BookingItem;
import com.beautysalon.entity.User;
import com.beautysalon.enums.BookingStatus;
import com.beautysalon.enums.Role;
import com.beautysalon.exception.BadRequestException;
import com.beautysalon.exception.ResourceNotFoundException;
import com.beautysalon.repository.BookingItemRepository;
import com.beautysalon.repository.BookingRepository;
import com.beautysalon.repository.ServiceRepository;
import com.beautysalon.repository.UserRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public BookingService(BookingRepository bookingRepository,
                          BookingItemRepository bookingItemRepository,
                          UserRepository userRepository,
                          ServiceRepository serviceRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingItemRepository = bookingItemRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long customerId) {

        // validacija staffa
        User staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff nije pronađen"));

        if (staff.getRole() != Role.STAFF) {
            throw new BadRequestException("Odabrani korisnik nije djelatnica");
        }

        // validacija usluga
        List<com.beautysalon.entity.Service> services = serviceRepository.findAllById(request.getServiceIds());

        if (services.size() != request.getServiceIds().size()) {
            throw new ResourceNotFoundException("Jedna ili više usluga nije pronađena");
        }

        for (com.beautysalon.entity.Service service : services) {
            if (!service.getStaffId().equals(staff.getId())) {
                throw new BadRequestException("Usluga " + service.getName() + " ne pripada odabranoj djelatnici");
            }
        }

        // racunanje cijene i trajanja
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalDurationMinutes = 0;

        for (com.beautysalon.entity.Service service : services) {
            totalPrice = totalPrice.add(service.getPrice());
            totalDurationMinutes += service.getDurationMinutes();
        }

        LocalTime endTime = request.getStartTime().plusMinutes(totalDurationMinutes);

        // provjera slobodnog termina
        List<Booking> existingBookings = bookingRepository.findByStaffIdAndDateAndStatusIn(
                staff.getId(), request.getDate(), List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));

        for (Booking existing : existingBookings) {
            boolean overlaps = request.getStartTime().isBefore(existing.getEndTime())
                    && endTime.isAfter(existing.getStartTime());

            if (overlaps) {
                throw new BadRequestException("Odabrani termin nije slobodan");
            }
        }

        // kreiranje i spremanje bookinga
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setStaff(staff);
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(endTime);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(totalPrice);
        booking.setTotalDurationMinutes(totalDurationMinutes);
        booking.setCreatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        // kreiranje i spremanje booking_items (snapshot)
        List<BookingItemResponse> itemResponses = new ArrayList<>();

        for (com.beautysalon.entity.Service service : services) {
            BookingItem item = new BookingItem();
            item.setBooking(booking);
            item.setService(service);
            item.setServiceName(service.getName());
            item.setPrice(service.getPrice());
            item.setDurationMinutes(service.getDurationMinutes());

            bookingItemRepository.save(item);

            itemResponses.add(new BookingItemResponse(
                    service.getId(),
                    service.getName(),
                    service.getPrice(),
                    service.getDurationMinutes()
            ));
        }

        // mapiranje u response
        // mapiranje u response
        return new BookingResponse(
                booking.getId(),
                customer.getId(),
                customer.getFullName(),
                staff.getId(),
                staff.getFullName(),
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getTotalDurationMinutes(),
                booking.getCreatedAt(),
                itemResponses
        );
    }

    public List<BookingResponse> getMyBookings(Long customerId) {

        List<Booking> bookings = bookingRepository.findByCustomerId(customerId);
        List<BookingResponse> responses = new ArrayList<>();

        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);

            List<BookingItem> items = bookingItemRepository.findByBookingId(booking.getId());
            List<BookingItemResponse> itemResponses = new ArrayList<>();

            for (int k = 0; k < items.size(); k++) {
                BookingItem item = items.get(k);
                itemResponses.add(new BookingItemResponse(
                        item.getService() != null ? item.getService().getId() : null,
                        item.getServiceName(),
                        item.getPrice(),
                        item.getDurationMinutes()
                ));
            }

            responses.add(new BookingResponse(
                    booking.getId(),
                    booking.getCustomer().getId(),
                    booking.getCustomer().getFullName(),
                    booking.getStaff().getId(),
                    booking.getStaff().getFullName(),
                    booking.getDate(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    booking.getStatus(),
                    booking.getTotalPrice(),
                    booking.getTotalDurationMinutes(),
                    booking.getCreatedAt(),
                    itemResponses
            ));
        }

        return responses;
    }

    public List<BookingResponse> getStaffBookings(Long staffId, LocalDate date) {

        List<Booking> bookings = bookingRepository.findByStaffIdAndDate(staffId, date);
        List<BookingResponse> responses = new ArrayList<>();

        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);

            List<BookingItem> items = bookingItemRepository.findByBookingId(booking.getId());
            List<BookingItemResponse> itemResponses = new ArrayList<>();

            for (int k = 0; k < items.size(); k++) {
                BookingItem item = items.get(k);
                itemResponses.add(new BookingItemResponse(
                        item.getService() != null ? item.getService().getId() : null,
                        item.getServiceName(),
                        item.getPrice(),
                        item.getDurationMinutes()
                ));
            }

            responses.add(new BookingResponse(
                    booking.getId(),
                    booking.getCustomer().getId(),
                    booking.getCustomer().getFullName(),
                    booking.getStaff().getId(),
                    booking.getStaff().getFullName(),
                    booking.getDate(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    booking.getStatus(),
                    booking.getTotalPrice(),
                    booking.getTotalDurationMinutes(),
                    booking.getCreatedAt(),
                    itemResponses
            ));
        }

        return responses;
    }

    @Transactional
    public BookingResponse confirmBooking(Long bookingId, Long staffId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezervacija nije pronađena"));

        if (!booking.getStaff().getId().equals(staffId)) {
            throw new BadRequestException("Nemate pravo mijenjati ovu rezervaciju");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Samo rezervacije na čekanju mogu biti potvrđene");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        List<BookingItem> items = bookingItemRepository.findByBookingId(booking.getId());
        List<BookingItemResponse> itemResponses = new ArrayList<>();

        for (int k = 0; k < items.size(); k++) {
            BookingItem item = items.get(k);
            itemResponses.add(new BookingItemResponse(
                    item.getService() != null ? item.getService().getId() : null,
                    item.getServiceName(),
                    item.getPrice(),
                    item.getDurationMinutes()
            ));
        }

        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getCustomer().getFullName(),
                booking.getStaff().getId(),
                booking.getStaff().getFullName(),
                booking.getDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getTotalDurationMinutes(),
                booking.getCreatedAt(),
                itemResponses
        );
    }
}