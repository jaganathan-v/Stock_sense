package com.stocksense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LocationRequestDto {
    @NotBlank(message = "Location name is required")
    @Size(max = 120, message = "Location name cannot exceed 120 characters")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
