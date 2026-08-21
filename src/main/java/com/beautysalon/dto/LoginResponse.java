package com.beautysalon.dto;

import com.beautysalon.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String fullName;
    private Role role;
    private String phone;
    private Boolean needsPhoneNumber;
}