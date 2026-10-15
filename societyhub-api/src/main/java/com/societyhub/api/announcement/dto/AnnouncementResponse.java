package com.societyhub.api.announcement.dto;

import com.societyhub.api.announcement.Announcement;
import com.societyhub.api.announcement.AnnouncementPriority;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnnouncementResponse {
    private Long id;
    private Long societyId;
    private String societyName;
    private String title;
    private String content;
    private AnnouncementPriority priority;
    private LocalDateTime createdAt;

    public static AnnouncementResponse fromEntity(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .societyId(announcement.getSociety().getId())
                .societyName(announcement.getSociety().getName())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .priority(announcement.getPriority())
                .createdAt(announcement.getCreatedAt())
                .build();
    }
}