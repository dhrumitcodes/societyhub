package com.societyhub.api.user;

import com.societyhub.api.common.exception.ResourceNotFoundException;
import com.societyhub.api.membership.MembershipRepository;
import com.societyhub.api.security.SocietySecurityEvaluator;
import com.societyhub.api.user.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final SocietySecurityEvaluator societySecurity;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var memberships = membershipRepository.findByUserId(user.getId()).stream()
                .map(m -> MeResponse.MembershipSummary.builder()
                        .societyId(m.getSociety().getId())
                        .societyName(m.getSociety().getName())
                        .role(m.getSocietyRole())
                        .canManage(societySecurity.isSocietyOfficerOrAdmin(m.getSociety().getId()))
                        .build())
                .toList();

        return ResponseEntity.ok(MeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .memberships(memberships)
                .build());
    }
}