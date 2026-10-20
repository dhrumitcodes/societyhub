package com.societyhub.api.event.dto;

import com.societyhub.api.event.EventRegistration;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String userEmail;
    private String userFullName;

    public static RegistrationResponse fromEntity(EventRegistration r) {
        return RegistrationResponse.builder()
                .id(r.getId())
                .eventId(r.getEvent().getId())
                .eventTitle(r.getEvent().getTitle())
                .userId(r.getUser().getId())
                .userEmail(r.getUser().getEmail())
                .userFullName(r.getUser().getFullName())
                .build();
    }
}