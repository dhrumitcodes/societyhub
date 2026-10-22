package com.societyhub.api.event;

import com.societyhub.api.event.dto.EventRequest;
import com.societyhub.api.event.dto.EventResponse;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import com.societyhub.api.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private SocietyRepository societyRepository;
    @Mock private EventRegistrationRepository registrationRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private EventService service;

    private Society society;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext(); // no signed-in user unless a test says so
        society = mock(Society.class);
        when(society.getId()).thenReturn(1L);
        when(society.getName()).thenReturn("Coding Club");
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private EventRequest request(Integer capacity) {
        EventRequest r = new EventRequest();
        r.setTitle("Workshop");
        r.setStartTime(LocalDateTime.now().plusDays(2));
        r.setEndTime(LocalDateTime.now().plusDays(2).plusHours(2));
        r.setCapacity(capacity);
        return r;
    }

    private Event existingEvent() {
        return Event.builder().id(10L).society(society).title("Old title").capacity(50)
                .startTime(LocalDateTime.now().plusDays(1)).endTime(LocalDateTime.now().plusDays(1).plusHours(2)).build();
    }

    @Test
    void createRejectsAnEndTimeBeforeTheStart() {
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));
        EventRequest r = request(null);
        r.setEndTime(r.getStartTime().minusHours(1));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.createEvent(1L, r));

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void createFailsForAnUnknownSociety() {
        when(societyRepository.findById(9L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.createEvent(9L, request(null)));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void createKeepsTheDefaultCapacityWhenNoneIsSent() {
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));

        EventResponse response = service.createEvent(1L, request(null));

        assertEquals(50, response.getCapacity());
    }

    @Test
    void createUsesTheRequestedCapacity() {
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));

        EventResponse response = service.createEvent(1L, request(120));

        assertEquals(120, response.getCapacity());
    }

    @Test
    void updateRefusesACapacityBelowTheNumberAlreadyRegistered() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existingEvent()));
        when(registrationRepository.countByEventId(10L)).thenReturn(12L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.updateEvent(10L, request(10)));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("12"));
    }

    @Test
    void updateAllowsACapacityEqualToTheRegisteredCount() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existingEvent()));
        when(registrationRepository.countByEventId(10L)).thenReturn(12L);

        EventResponse response = service.updateEvent(10L, request(12));

        assertEquals(12, response.getCapacity());
        assertEquals("Workshop", response.getTitle());
    }

    @Test
    void updateKeepsTheCapacityWhenNoneIsSent() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existingEvent()));

        EventResponse response = service.updateEvent(10L, request(null));

        assertEquals(50, response.getCapacity());
    }

    @Test
    void deleteFailsForAnUnknownEvent() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.deleteEvent(99L));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void responseCarriesTheRegisteredCountAndNotRegisteredForAnonymousVisitors() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existingEvent()));
        when(registrationRepository.countByEventId(10L)).thenReturn(3L);

        EventResponse response = service.getEventById(10L);

        assertEquals(3L, response.getRegisteredCount());
        assertFalse(response.isRegistered());
    }
}