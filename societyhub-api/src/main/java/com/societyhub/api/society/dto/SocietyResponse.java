package com.societyhub.api.society.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SocietyResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private LocalDateTime createdAt;
}