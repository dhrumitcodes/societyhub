package com.societyhub.api.auth.dto;

import com.societyhub.api.user.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private Role role;
}