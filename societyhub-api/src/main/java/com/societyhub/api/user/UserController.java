package com.societyhub.api.user;

import com.societyhub.api.common.exception.ResourceNotFoundException;
import com.societyhub.api.membership.MembershipRepository;
import com.societyhub.api.security.SocietySecurityEvaluator;
import com.societyhub.api.user.dto.ChangePasswordRequest;
import com.societyhub.api.user.dto.MeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final SocietySecurityEvaluator societySecurity;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        User user = findCurrent(authentication);

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
    @PostMapping("/me/password")
    @Transactional
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                               Authentication authentication) {
        User user = findCurrent(authentication);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Your current password is incorrect.");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Your new password must be different from the current one.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    private User findCurrent(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}