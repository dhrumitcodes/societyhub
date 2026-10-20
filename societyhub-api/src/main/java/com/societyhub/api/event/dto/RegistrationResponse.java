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
    private Long societyId;
    private String societyName;
    private LocalDateTime eventStartTime;
    private LocalDateTime eventEndTime;
    private Long userId;
    private String userEmail;
    private String userFullName;

    public static RegistrationResponse fromEntity(EventRegistration r) {
        return RegistrationResponse.builder()
                .id(r.getId())
                .eventId(r.getEvent().getId())
                .eventTitle(r.getEvent().getTitle())
                .societyId(r.getEvent().getSociety().getId())
                .societyName(r.getEvent().getSociety().getName())
                .eventStartTime(r.getEvent().getStartTime())
                .eventEndTime(r.getEvent().getEndTime())
                .userId(r.getUser().getId())
                .userEmail(r.getUser().getEmail())
                .userFullName(r.getUser().getFullName())
                .build();
    }
}