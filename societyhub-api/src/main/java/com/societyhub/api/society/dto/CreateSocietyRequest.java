package com.societyhub.api.society.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSocietyRequest {
    @NotBlank(message = "Society name is required")
    private String name;
    private String description;
    private String category;
}