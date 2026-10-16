package com.societyhub.api.event;

import com.societyhub.api.event.dto.EventRequest;
import com.societyhub.api.event.dto.EventResponse;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SocietyRepository societyRepository;

    @InjectMocks
    private EventService eventService;

    private Society society;
    private EventRequest validRequest;

    @BeforeEach
    void setUp() {
        society = Society.builder().id(1L).name("Coding Club").build();

        validRequest = new EventRequest();
        validRequest.setTitle("Tech Talk");
        validRequest.setDescription("Intro to Java");
        validRequest.setLocation("Room 101");
        validRequest.setStartTime(LocalDateTime.now().plusDays(2));
        validRequest.setEndTime(LocalDateTime.now().plusDays(2).plusHours(2));
    }

    @Test
    void createEvent_Success() {
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            e.setId(10L);
            return e;
        });

        EventResponse response = eventService.createEvent(1L, validRequest);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Tech Talk", response.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_InvalidTimes_ThrowsException() {
        validRequest.setEndTime(validRequest.getStartTime().minusHours(1));
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));

        assertThrows(ResponseStatusException.class, () -> eventService.createEvent(1L, validRequest));
        verify(eventRepository, never()).save(any(Event.class));
    }
}