package com.societyhub.api.announcement;

import com.societyhub.api.announcement.dto.AnnouncementRequest;
import com.societyhub.api.announcement.dto.AnnouncementResponse;
import com.societyhub.api.common.exception.ResourceNotFoundException;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final SocietyRepository societyRepository;

    @Transactional
    public AnnouncementResponse createAnnouncement(Long societyId, AnnouncementRequest request) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found"));

        Announcement announcement = Announcement.builder()
                .society(society)
                .title(request.getTitle())
                .content(request.getContent())
                .priority(request.getPriority())
                .build();

        return AnnouncementResponse.fromEntity(announcementRepository.save(announcement));
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getSocietyAnnouncements(Long societyId) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found");
        }
        return announcementRepository.findBySocietyIdOrderByCreatedAtDesc(societyId).stream()
                .map(AnnouncementResponse::fromEntity)
                .toList();
    }
    @Transactional
    public void deleteAnnouncement(Long societyId, Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));
        if (!announcement.getSociety().getId().equals(societyId)) {
            throw new ResourceNotFoundException("Announcement not found");
        }
        announcementRepository.delete(announcement);
    }
}