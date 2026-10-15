package com.societyhub.api.event;

import com.societyhub.api.event.dto.RegistrationResponse;
import com.societyhub.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService registrationService;

    @PostMapping("/{eventId}/register")
    public ResponseEntity<RegistrationResponse> registerForEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.registerForEvent(eventId, user));
    }

    @DeleteMapping("/{eventId}/register")
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user) {
        registrationService.cancelRegistration(eventId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/attendees")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(T(com.societyhub.api.event.EventRepository).cast(base).findSocietyIdByEventId(#eventId))") // Alternatively check via service
    public ResponseEntity<List<RegistrationResponse>> getEventAttendees(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getEventRegistrations(eventId));
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<List<RegistrationResponse>> getMyRegistrations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(registrationService.getUserRegistrations(user));
    }
}