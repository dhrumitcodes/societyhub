package com.societyhub.api.announcement.dto;

import com.societyhub.api.announcement.AnnouncementPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotNull(message = "Priority is mandatory")
    private AnnouncementPriority priority = AnnouncementPriority.NORMAL;
}