package com.societyhub.api.event;

import com.societyhub.api.event.dto.EventRequest;
import com.societyhub.api.event.dto.EventResponse;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SocietyRepository societyRepository;

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

        return EventResponse.fromEntity(eventRepository.save(event));
    }

    public Page<EventResponse> getFilteredEvents(Long societyId, String search, String location, Boolean upcoming, Pageable pageable) {
        Specification<Event> spec = Specification.where(EventSpecification.hasSocietyId(societyId))
                .and(EventSpecification.titleContains(search))
                .and(EventSpecification.locationContains(location))
                .and(EventSpecification.isUpcoming(upcoming));

        return eventRepository.findAll(spec, pageable).map(EventResponse::fromEntity);
    }

    public EventResponse getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return EventResponse.fromEntity(event);
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());

        return EventResponse.fromEntity(eventRepository.save(event));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        eventRepository.deleteById(eventId);
    }
}