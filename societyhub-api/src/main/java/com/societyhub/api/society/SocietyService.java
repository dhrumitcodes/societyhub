package com.societyhub.api.society;

import com.societyhub.api.common.exception.ResourceNotFoundException;
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
            throw new IllegalArgumentException("A society named '" + request.getName() + "' already exists.");
        }

        Society society = Society.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .build();

        return mapToResponse(societyRepository.save(society));
    }

    @Transactional(readOnly = true)
    public List<SocietyResponse> getAllSocieties() {
        return societyRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SocietyResponse getSocietyById(Long id) {
        return mapToResponse(find(id));
    }

    @Transactional
    public SocietyResponse updateSociety(Long id, CreateSocietyRequest request) {
        Society society = find(id);
        society.setName(request.getName());
        society.setDescription(request.getDescription());
        society.setCategory(request.getCategory());
        return mapToResponse(societyRepository.save(society));
    }

    @Transactional
    public void deleteSociety(Long id) {
        if (!societyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Society not found");
        }
        societyRepository.deleteById(id);
    }

    private Society find(Long id) {
        return societyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found"));
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