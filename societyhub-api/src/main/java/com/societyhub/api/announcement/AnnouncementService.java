package com.societyhub.api.announcement;

import com.societyhub.api.announcement.dto.AnnouncementRequest;
import com.societyhub.api.announcement.dto.AnnouncementResponse;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final SocietyRepository societyRepository;

    @Transactional
    public AnnouncementResponse createAnnouncement(Long societyId, AnnouncementRequest request) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Society not found"));

        Announcement announcement = Announcement.builder()
                .society(society)
                .title(request.getTitle())
                .content(request.getContent())
                .priority(request.getPriority())
                .build();

        return AnnouncementResponse.fromEntity(announcementRepository.save(announcement));
    }

    public List<AnnouncementResponse> getSocietyAnnouncements(Long societyId) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Society not found");
        }
        return announcementRepository.findBySocietyIdOrderByCreatedAtDesc(societyId).stream()
                .map(AnnouncementResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        if (!announcementRepository.existsById(announcementId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
        }
        announcementRepository.deleteById(announcementId);
    }
}