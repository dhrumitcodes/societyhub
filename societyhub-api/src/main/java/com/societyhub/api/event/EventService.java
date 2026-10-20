package com.societyhub.api.event;

import com.societyhub.api.event.dto.EventRequest;
import com.societyhub.api.event.dto.EventResponse;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import com.societyhub.api.user.UserRepository;
import com.societyhub.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SocietyRepository societyRepository;
    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventResponse createEvent(Long societyId, EventRequest request) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Society not found"));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }

        Event event = Event.builder()
                .society(society)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
        if (request.getCapacity() != null) {
            event.setCapacity(request.getCapacity());
        }

        return toResponse(eventRepository.save(event), currentUserId());
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getFilteredEvents(Long societyId, String search, String location, Boolean upcoming, Pageable pageable) {
        Specification<Event> spec = Specification.where(EventSpecification.hasSocietyId(societyId))
                .and(EventSpecification.titleContains(search))
                .and(EventSpecification.locationContains(location))
                .and(EventSpecification.isUpcoming(upcoming));

        Long userId = currentUserId();
        return eventRepository.findAll(spec, pageable).map(e -> toResponse(e, userId));
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return toResponse(event, currentUserId());
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }
        if (request.getCapacity() != null) {
            long registered = registrationRepository.countByEventId(eventId);
            if (request.getCapacity() < registered) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Capacity cannot be lower than the " + registered + " people already registered");
            }
            event.setCapacity(request.getCapacity());
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());

        return toResponse(eventRepository.save(event), currentUserId());
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        eventRepository.deleteById(eventId);
    }

    private EventResponse toResponse(Event event, Long userId) {
        long count = registrationRepository.countByEventId(event.getId());
        boolean registered = userId != null && registrationRepository.existsByEventIdAndUserId(event.getId(), userId);
        return EventResponse.fromEntity(event, count, registered);
    }
    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).map(User::getId).orElse(null);
    }
}