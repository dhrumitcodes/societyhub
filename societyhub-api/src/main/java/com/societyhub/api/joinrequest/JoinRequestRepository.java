package com.societyhub.api.joinrequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {

    boolean existsByUserIdAndSocietyIdAndStatus(Long userId, Long societyId, JoinRequestStatus status);

    List<JoinRequest> findBySocietyIdAndStatusOrderByCreatedAtAsc(Long societyId, JoinRequestStatus status);

    List<JoinRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
}