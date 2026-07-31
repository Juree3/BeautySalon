package com.beautysalon.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    @Size(max = 255)
    private String fullName;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 255)
    private String password;
    private String phone;
}