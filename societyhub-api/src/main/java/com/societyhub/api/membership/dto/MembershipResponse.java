package com.societyhub.api.membership.dto;

import com.societyhub.api.membership.SocietyRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MembershipResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private Long societyId;
    private String societyName;
    private SocietyRole role;
    private LocalDateTime joinedAt;
}