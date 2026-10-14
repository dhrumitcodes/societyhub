package com.societyhub.api.event;

import com.societyhub.api.event.dto.EventRequest;
import com.societyhub.api.event.dto.EventResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/societies/{societyId}/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<EventResponse> createEvent(
            @PathVariable Long societyId,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(societyId, request));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getSocietyEvents(@PathVariable Long societyId) {
        return ResponseEntity.ok(eventService.getEventsBySociety(societyId));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Long societyId,
            @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long societyId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, request));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long societyId,
            @PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}