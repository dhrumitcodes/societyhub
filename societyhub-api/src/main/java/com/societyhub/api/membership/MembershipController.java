package com.societyhub.api.membership;

import com.societyhub.api.membership.dto.AddMemberRequest;
import com.societyhub.api.membership.dto.MembershipResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/societies/{societyId}/members")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<MembershipResponse> addMember(
            @PathVariable Long societyId,
            @Valid @RequestBody AddMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.addMember(societyId, request));
    }

    @GetMapping
    public ResponseEntity<List<MembershipResponse>> getSocietyMembers(@PathVariable Long societyId) {
        return ResponseEntity.ok(membershipService.getSocietyMembers(societyId));
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("@societySecurity.isSocietyPresident(#societyId)")
    public ResponseEntity<MembershipResponse> updateMemberRole(
            @PathVariable Long societyId,
            @PathVariable Long userId,
            @RequestParam SocietyRole role) {
        return ResponseEntity.ok(membershipService.updateMemberRole(societyId, userId, role));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long societyId,
            @PathVariable Long userId) {
        membershipService.removeMember(societyId, userId);
        return ResponseEntity.noContent().build();
    }
}