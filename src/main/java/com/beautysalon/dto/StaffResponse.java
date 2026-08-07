package com.beautysalon.dto;

import com.beautysalon.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StaffResponse {

    private Long id;
    private String fullName;
    private Category category;
}
