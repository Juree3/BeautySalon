package com.beautysalon.service;

import com.beautysalon.dto.ServiceResponse;
import com.beautysalon.dto.WorkSlotRequest;
import com.beautysalon.dto.WorkSlotResponse;
import com.beautysalon.entity.User;
import com.beautysalon.entity.WorkSlot;
import com.beautysalon.repository.UserRepository;
import com.beautysalon.repository.WorkSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkSlotService {


    private WorkSlotRepository workSlotRepository;
    private UserRepository userRepository;
    public WorkSlotService(WorkSlotRepository workSlotRepository,  UserRepository userRepository) {
        this.workSlotRepository = workSlotRepository;
        this.userRepository = userRepository;
    }

    public WorkSlotResponse createWorkSlot(WorkSlotRequest request) {

        if(request.getEndTime().isBefore(request.getStartTime())) {
            throw new RuntimeException("Vrijeme završetka mora biti nakon početka");
        }

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();


        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));


        WorkSlot workSlot = new WorkSlot();

        workSlot.setStaff(currentUser);
        workSlot.setDate(request.getDate());
        workSlot.setStartTime(request.getStartTime());
        workSlot.setEndTime(request.getEndTime());



        WorkSlot saved = workSlotRepository.save(workSlot);


        return new WorkSlotResponse(
                saved.getId(),
                saved.getStaff().getId(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime()
        );
    }

    public List<WorkSlotResponse> getMySlots() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        return workSlotRepository.findByStaffId(currentUser.getId())
                .stream()
                .map(slot -> new WorkSlotResponse(
                        slot.getId(),
                        slot.getStaff().getId(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime()
                ))
                .toList();
    }

    public List<WorkSlotResponse> getWorkSlotsByStaff(Long staffId) {

        List<WorkSlot> workSlots = workSlotRepository.findByStaffId(staffId);

        return workSlots.stream()
                .map(slot -> new WorkSlotResponse(
                        slot.getId(),
                        slot.getStaff().getId(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime()
                ))
                .toList();
    }

    public List<WorkSlotResponse> getWorkSlotsByStaffAndDate(Long staffId, LocalDate date) {

        List<WorkSlot> workSlots = workSlotRepository.findByStaffIdAndDate(staffId, date);

        return workSlots.stream()
                .map(slot -> new WorkSlotResponse(
                        slot.getId(),
                        slot.getStaff().getId(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime()
                ))
                .toList();
    }

    public List<WorkSlotResponse> getWorkSlotsBetweenDates(
            Long staffId,
            LocalDate from,
            LocalDate to
    ) {

        List<WorkSlot> workSlots =
                workSlotRepository.findByStaffIdAndDateBetween(staffId, from, to);

        return workSlots.stream()
                .map(slot -> new WorkSlotResponse(
                        slot.getId(),
                        slot.getStaff().getId(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime()
                ))
                .toList();
    }

    public void deleteWorkSlot(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        WorkSlot workSlot = workSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot nije pronađen"));

        if (!workSlot.getStaff().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Nije tvoj slot");
        }
        workSlotRepository.delete(workSlot);
    }
}
