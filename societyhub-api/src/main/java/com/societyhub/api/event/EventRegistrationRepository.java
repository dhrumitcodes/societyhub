package com.societyhub.api.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);
    long countByEventId(Long eventId);
    List<EventRegistration> findByUserId(Long userId);
}