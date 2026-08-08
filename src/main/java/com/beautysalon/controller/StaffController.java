package com.beautysalon.controller;

import com.beautysalon.dto.StaffResponse;
import com.beautysalon.enums.Category;
import com.beautysalon.service.StaffService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/by-category/{category}")
    public List<StaffResponse> getByCategory(@PathVariable String category) {
        return staffService.getByCategory(Category.valueOf(category.toUpperCase()));
    }
}