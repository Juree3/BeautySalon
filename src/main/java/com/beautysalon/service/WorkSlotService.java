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

@Service
public class WorkSlotService {


    private WorkSlotRepository workSlotRepository;
    private UserRepository userRepository;
    public WorkSlotService(WorkSlotRepository workSlotRepository,  UserRepository userRepository) {
        this.workSlotRepository = workSlotRepository;
        this.userRepository = userRepository;
    }

    public WorkSlotResponse createWorkSlot(WorkSlotRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        WorkSlot workSlot = new WorkSlot();
        workSlot.setDate(request.getDate());
        workSlot.setStartTime(request.getStartTime());
        workSlot.setEndTime(request.getEndTime());

        WorkSlot saved = workSlotRepository.save(workSlot);

        return new WorkSlotResponse(
                saved.getId(),
                saved.getStaffId(),
                saved.getDate(),
                saved.getStartTime(),
                saved.getEndTime()
        );
    }
}
