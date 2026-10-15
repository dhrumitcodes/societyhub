package com.societyhub.api.event;

import com.societyhub.api.event.dto.EventRequest;
import com.societyhub.api.event.dto.EventResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/societies/{societyId}/events")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<EventResponse> createEvent(
            @PathVariable Long societyId,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(societyId, request));
    }

    @GetMapping("/societies/{societyId}/events")
    public ResponseEntity<Page<EventResponse>> getSocietyEvents(
            @PathVariable Long societyId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean upcoming,
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable) {
        return ResponseEntity.ok(eventService.getFilteredEvents(societyId, search, location, upcoming, pageable));
    }

    @GetMapping("/events")
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean upcoming,
            @PageableDefault(size = 10, sort = "startTime") Pageable pageable) {
        return ResponseEntity.ok(eventService.getFilteredEvents(null, search, location, upcoming, pageable));
    }

    @GetMapping("/societies/{societyId}/events/{eventId}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Long societyId,
            @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @PutMapping("/societies/{societyId}/events/{eventId}")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long societyId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, request));
    }

    @DeleteMapping("/societies/{societyId}/events/{eventId}")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long societyId,
            @PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
}