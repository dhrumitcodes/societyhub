package com.societyhub.api.joinrequest;

import com.societyhub.api.joinrequest.dto.CreateJoinRequest;
import com.societyhub.api.joinrequest.dto.JoinRequestResponse;
import com.societyhub.api.user.User;
import com.societyhub.api.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JoinRequestController {

    private final JoinRequestService joinRequestService;
    private final UserRepository userRepository;

    @PostMapping("/societies/{societyId}/join-requests")
    public ResponseEntity<JoinRequestResponse> requestToJoin(
            @PathVariable Long societyId,
            @Valid @RequestBody(required = false) CreateJoinRequest body,
            Authentication auth) {
        String message = body == null ? null : body.getMessage();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(joinRequestService.create(societyId, currentUser(auth), message));
    }

    @GetMapping("/societies/{societyId}/join-requests")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<List<JoinRequestResponse>> getPending(@PathVariable Long societyId) {
        return ResponseEntity.ok(joinRequestService.getPending(societyId));
    }

    @PostMapping("/societies/{societyId}/join-requests/{requestId}/approve")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<JoinRequestResponse> approve(@PathVariable Long societyId, @PathVariable Long requestId) {
        return ResponseEntity.ok(joinRequestService.approve(societyId, requestId));
    }

    @PostMapping("/societies/{societyId}/join-requests/{requestId}/decline")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<JoinRequestResponse> decline(@PathVariable Long societyId, @PathVariable Long requestId) {
        return ResponseEntity.ok(joinRequestService.decline(societyId, requestId));
    }

    @GetMapping("/join-requests/mine")
    public ResponseEntity<List<JoinRequestResponse>> mine(Authentication auth) {
        return ResponseEntity.ok(joinRequestService.getForUser(currentUser(auth).getId()));
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}