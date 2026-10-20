package com.societyhub.api.user.dto;

import com.societyhub.api.membership.SocietyRole;
import com.societyhub.api.user.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MeResponse {
    private Long id;
    private String email;
    private String fullName;
    private Role role;
    private List<MembershipSummary> memberships;

    @Data
    @Builder
    public static class MembershipSummary {
        private Long societyId;
        private String societyName;
        private SocietyRole role;
        private boolean canManage;
    }
}