package com.societyhub.api.event;

import com.societyhub.api.event.dto.RegistrationResponse;
import com.societyhub.api.society.Society;
import com.societyhub.api.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventRegistrationServiceTest {

    @Mock private EventRegistrationRepository registrationRepository;
    @Mock private EventRepository eventRepository;
    @InjectMocks private EventRegistrationService service;

    private User user;
    private Society society;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        society = mock(Society.class);
        when(society.getId()).thenReturn(1L);
        when(society.getName()).thenReturn("Coding Club");
        when(registrationRepository.save(any(EventRegistration.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Event upcomingEvent(int capacity) {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        return Event.builder().id(10L).society(society).title("Hackathon")
                .capacity(capacity).startTime(start).endTime(start.plusHours(3)).build();
    }

    private int statusOf(ResponseStatusException e) {
        return e.getStatusCode().value();
    }

    @Test
    void registersWhenSeatsAreAvailable() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(upcomingEvent(5)));
        when(registrationRepository.existsByEventIdAndUserId(10L, 7L)).thenReturn(false);
        when(registrationRepository.countByEventId(10L)).thenReturn(3L);

        RegistrationResponse result = service.registerForEvent(10L, user);

        assertEquals(10L, result.getEventId());
        assertEquals("Hackathon", result.getEventTitle());
        verify(registrationRepository).save(any(EventRegistration.class));
    }

    @Test
    void allowsTheLastSeat() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(upcomingEvent(5)));
        when(registrationRepository.countByEventId(10L)).thenReturn(4L);

        service.registerForEvent(10L, user);

        verify(registrationRepository).save(any(EventRegistration.class));
    }

    @Test
    void rejectsWhenTheEventIsFull() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(upcomingEvent(5)));
        when(registrationRepository.countByEventId(10L)).thenReturn(5L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.registerForEvent(10L, user));

        assertEquals(400, statusOf(ex));
        verify(registrationRepository, never()).save(any(EventRegistration.class));
    }

    @Test
    void rejectsDoubleRegistration() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(upcomingEvent(5)));
        when(registrationRepository.existsByEventIdAndUserId(10L, 7L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.registerForEvent(10L, user));

        assertEquals(409, statusOf(ex));
        verify(registrationRepository, never()).save(any(EventRegistration.class));
    }

    @Test
    void rejectsEventsThatHaveAlreadyEnded() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        Event past = Event.builder().id(10L).society(society).title("Old").capacity(5)
                .startTime(start).endTime(start.plusHours(2)).build();
        when(eventRepository.findById(10L)).thenReturn(Optional.of(past));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.registerForEvent(10L, user));

        assertEquals(400, statusOf(ex));
    }

    @Test
    void rejectsUnknownEvent() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.registerForEvent(99L, user));

        assertEquals(404, statusOf(ex));
    }

    @Test
    void cancelFailsWhenNotRegistered() {
        when(registrationRepository.findByEventIdAndUserId(10L, 7L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.cancelRegistration(10L, user));

        assertEquals(404, statusOf(ex));
    }

    @Test
    void cancelDeletesTheRegistration() {
        EventRegistration registration = mock(EventRegistration.class);
        when(registrationRepository.findByEventIdAndUserId(10L, 7L)).thenReturn(Optional.of(registration));

        service.cancelRegistration(10L, user);

        verify(registrationRepository).delete(registration);
    }
}