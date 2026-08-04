package com.beautysalon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceRequest {

    @NotBlank
    private String name;

    @Size(max = 256)
    private String description;

    @NotNull
    @Positive
    private Integer durationMinutes;

    @NotNull
    @Positive
    private BigDecimal price;

    private String imageUrl;
}