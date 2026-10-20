package com.societyhub.api.event.dto;

import com.societyhub.api.event.Event;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponse {
    private Long id;
    private Long societyId;
    private String societyName;
    private String title;
    private String description;
    private String location;
    private Integer capacity;
    private long registeredCount;
    private boolean registered;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;

    public static EventResponse fromEntity(Event event, long registeredCount, boolean registered) {
        return EventResponse.builder()
                .id(event.getId())
                .societyId(event.getSociety().getId())
                .societyName(event.getSociety().getName())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .capacity(event.getCapacity())
                .registeredCount(registeredCount)
                .registered(registered)
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .createdAt(event.getCreatedAt())
                .build();
    }

    public static EventResponse fromEntity(Event event) {
        return fromEntity(event, 0, false);
    }
}