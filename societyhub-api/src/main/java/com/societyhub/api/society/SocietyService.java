package com.societyhub.api.society;

import com.societyhub.api.society.dto.CreateSocietyRequest;
import com.societyhub.api.society.dto.SocietyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocietyService {

    private final SocietyRepository societyRepository;

    @Transactional
    public SocietyResponse createSociety(CreateSocietyRequest request) {
        if (societyRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Society with name '" + request.getName() + "' already exists.");
        }

        Society society = Society.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .build();

        Society saved = societyRepository.save(society);
        return mapToResponse(saved);
    }

    public List<SocietyResponse> getAllSocieties() {
        return societyRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SocietyResponse getSocietyById(Long id) {
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Society not found with id: " + id));
        return mapToResponse(society);
    }

    @Transactional
    public SocietyResponse updateSociety(Long id, CreateSocietyRequest request) {
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Society not found with id: " + id));

        society.setName(request.getName());
        society.setDescription(request.getDescription());
        society.setCategory(request.getCategory());

        return mapToResponse(societyRepository.save(society));
    }

    @Transactional
    public void deleteSociety(Long id) {
        if (!societyRepository.existsById(id)) {
            throw new RuntimeException("Society not found with id: " + id);
        }
        societyRepository.deleteById(id);
    }

    private SocietyResponse mapToResponse(Society society) {
        return SocietyResponse.builder()
                .id(society.getId())
                .name(society.getName())
                .description(society.getDescription())
                .category(society.getCategory())
                .createdAt(society.getCreatedAt())
                .build();
    }
}