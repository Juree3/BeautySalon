package com.beautysalon.controller;

import com.beautysalon.dto.ServiceRequest;
import com.beautysalon.dto.ServiceResponse;
import com.beautysalon.dto.WorkSlotRequest;
import com.beautysalon.dto.WorkSlotResponse;
import com.beautysalon.service.WorkSlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-slots")
public class WorkSlotController {

    private final WorkSlotService workSlotService;

    public WorkSlotController(WorkSlotService workSlotService) {
        this.workSlotService = workSlotService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STAFF')")
    public WorkSlotResponse create(
            @Valid @RequestBody WorkSlotRequest request
    ) {
        return workSlotService.createWorkSlot(request);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STAFF')")
    public List<WorkSlotResponse> getMySlots() {
        return workSlotService.getMySlots();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('STAFF')")
    public void delete(@PathVariable Long id) {
        workSlotService.deleteWorkSlot(id);
    }

}