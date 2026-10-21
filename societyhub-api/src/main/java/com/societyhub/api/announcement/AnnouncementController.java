package com.societyhub.api.announcement;

import com.societyhub.api.announcement.dto.AnnouncementRequest;
import com.societyhub.api.announcement.dto.AnnouncementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/societies/{societyId}/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @PathVariable Long societyId,
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(announcementService.createAnnouncement(societyId, request));
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getSocietyAnnouncements(@PathVariable Long societyId) {
        return ResponseEntity.ok(announcementService.getSocietyAnnouncements(societyId));
    }

    @DeleteMapping("/{announcementId}")
    @PreAuthorize("@societySecurity.isSocietyOfficerOrAdmin(#societyId)")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Long societyId,
            @PathVariable Long announcementId) {
        announcementService.deleteAnnouncement(societyId, announcementId);
        return ResponseEntity.noContent().build();
    }
}