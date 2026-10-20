package com.societyhub.api.event;

import com.societyhub.api.event.dto.RegistrationResponse;
import com.societyhub.api.user.User;
import com.societyhub.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService registrationService;
    private final UserRepository userRepository;

    @PostMapping("/{eventId}/register")
    public ResponseEntity<RegistrationResponse> registerForEvent(@PathVariable Long eventId, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.registerForEvent(eventId, currentUser(auth)));
    }

    @DeleteMapping("/{eventId}/register")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long eventId, Authentication auth) {
        registrationService.cancelRegistration(eventId, currentUser(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/attendees")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(@eventService.getEventById(#eventId).societyId)")
    public ResponseEntity<List<RegistrationResponse>> getEventAttendees(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getEventRegistrations(eventId));
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<List<RegistrationResponse>> getMyRegistrations(Authentication auth) {
        return ResponseEntity.ok(registrationService.getUserRegistrations(currentUser(auth)));
    }

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}