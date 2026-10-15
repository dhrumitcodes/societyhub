package com.societyhub.api.event;

import com.societyhub.api.event.dto.RegistrationResponse;
import com.societyhub.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;

    @Transactional
    public RegistrationResponse registerForEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (event.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot register for past events");
        }

        if (registrationRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already registered for this event");
        }

        long currentRegistrations = registrationRepository.countByEventId(eventId);
        if (currentRegistrations >= event.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event is fully booked");
        }

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(user)
                .build();

        return RegistrationResponse.fromEntity(registrationRepository.save(registration));
    }

    @Transactional
    public void cancelRegistration(Long eventId, User user) {
        EventRegistration registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registration not found"));

        registrationRepository.delete(registration);
    }

    public List<RegistrationResponse> getEventRegistrations(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        return registrationRepository.findAll().stream()
                .filter(reg -> reg.getEvent().getId().equals(eventId))
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RegistrationResponse> getUserRegistrations(User user) {
        return registrationRepository.findByUserId(user.getId()).stream()
                .map(RegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}