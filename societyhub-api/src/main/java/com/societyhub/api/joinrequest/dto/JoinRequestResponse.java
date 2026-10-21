package com.societyhub.api.joinrequest.dto;

import com.societyhub.api.joinrequest.JoinRequest;
import com.societyhub.api.joinrequest.JoinRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JoinRequestResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private Long societyId;
    private String societyName;
    private JoinRequestStatus status;
    private String message;
    private LocalDateTime createdAt;

    public static JoinRequestResponse fromEntity(JoinRequest r) {
        return JoinRequestResponse.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userFullName(r.getUser().getFullName())
                .userEmail(r.getUser().getEmail())
                .societyId(r.getSociety().getId())
                .societyName(r.getSociety().getName())
                .status(r.getStatus())
                .message(r.getMessage())
                .createdAt(r.getCreatedAt())
                .build();
    }
}