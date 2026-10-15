package com.societyhub.api.event.dto;

import com.societyhub.api.event.EventRegistration;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegistrationResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String userEmail;
    private LocalDateTime registeredAt;

    public static RegistrationResponse fromEntity(EventRegistration reg) {
        return RegistrationResponse.builder()
                .id(reg.getId())
                .eventId(reg.getEvent().getId())
                .eventTitle(reg.getEvent().getTitle())
                .userId(reg.getUser().getId())
                .userEmail(reg.getUser().getEmail())
                .registeredAt(reg.getRegisteredAt())
                .build();
    }
}