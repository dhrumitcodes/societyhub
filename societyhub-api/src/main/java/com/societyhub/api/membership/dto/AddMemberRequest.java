package com.societyhub.api.membership.dto;

import com.societyhub.api.membership.SocietyRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberRequest {

    private String email;

    private Long userId;

    @NotNull(message = "Society role is required")
    private SocietyRole role;
}