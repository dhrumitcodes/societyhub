package com.societyhub.api.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    boolean existsByUserIdAndSocietyId(Long userId, Long societyId);
    Optional<Membership> findByUserIdAndSocietyId(Long userId, Long societyId);
    List<Membership> findBySocietyId(Long societyId);
    List<Membership> findByUserId(Long userId);
}