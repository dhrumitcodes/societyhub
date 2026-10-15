package com.societyhub.api.event;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class EventSpecification {

    public static Specification<Event> hasSocietyId(Long societyId) {
        return (root, query, cb) ->
                societyId == null ? cb.conjunction() : cb.equal(root.get("society").get("id"), societyId);
    }

    public static Specification<Event> titleContains(String search) {
        return (root, query, cb) ->
                (search == null || search.isBlank()) ? cb.conjunction() :
                        cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%");
    }

    public static Specification<Event> locationContains(String location) {
        return (root, query, cb) ->
                (location == null || location.isBlank()) ? cb.conjunction() :
                        cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<Event> isUpcoming(Boolean upcoming) {
        return (root, query, cb) -> {
            if (upcoming == null) return cb.conjunction();
            LocalDateTime now = LocalDateTime.now();
            return upcoming ? cb.greaterThanOrEqualTo(root.get("startTime"), now)
                    : cb.lessThan(root.get("startTime"), now);
        };
    }
}