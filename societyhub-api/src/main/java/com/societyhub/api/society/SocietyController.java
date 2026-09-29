package com.societyhub.api.society;

import com.societyhub.api.society.dto.CreateSocietyRequest;
import com.societyhub.api.society.dto.SocietyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/societies")
@RequiredArgsConstructor
public class SocietyController {

    private final SocietyService societyService;

    @PostMapping
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public ResponseEntity<SocietyResponse> createSociety(@Valid @RequestBody CreateSocietyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(societyService.createSociety(request));
    }

    @GetMapping
    public ResponseEntity<List<SocietyResponse>> getAllSocieties() {
        return ResponseEntity.ok(societyService.getAllSocieties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocietyResponse> getSocietyById(@PathVariable Long id) {
        return ResponseEntity.ok(societyService.getSocietyById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public ResponseEntity<SocietyResponse> updateSociety(
            @PathVariable Long id,
            @Valid @RequestBody CreateSocietyRequest request) {
        return ResponseEntity.ok(societyService.updateSociety(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COLLEGE_ADMIN')")
    public ResponseEntity<Void> deleteSociety(@PathVariable Long id) {
        societyService.deleteSociety(id);
        return ResponseEntity.noContent().build();
    }
}