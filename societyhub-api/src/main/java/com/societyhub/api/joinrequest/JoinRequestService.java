package com.societyhub.api.joinrequest;

import com.societyhub.api.common.exception.ResourceNotFoundException;
import com.societyhub.api.joinrequest.dto.JoinRequestResponse;
import com.societyhub.api.membership.MembershipRepository;
import com.societyhub.api.membership.MembershipService;
import com.societyhub.api.membership.SocietyRole;
import com.societyhub.api.membership.dto.AddMemberRequest;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import com.societyhub.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final SocietyRepository societyRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipService membershipService;

    @Transactional
    public JoinRequestResponse create(Long societyId, User user, String message) {
        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found"));

        if (membershipRepository.existsByUserIdAndSocietyId(user.getId(), societyId)) {
            throw new IllegalArgumentException("You are already a member of this society.");
        }
        if (joinRequestRepository.existsByUserIdAndSocietyIdAndStatus(user.getId(), societyId, JoinRequestStatus.PENDING)) {
            throw new IllegalArgumentException("You already have a pending request for this society.");
        }

        JoinRequest request = JoinRequest.builder()
                .user(user)
                .society(society)
                .message(message == null || message.isBlank() ? null : message.trim())
                .build();
        return JoinRequestResponse.fromEntity(joinRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<JoinRequestResponse> getPending(Long societyId) {
        return joinRequestRepository.findBySocietyIdAndStatusOrderByCreatedAtAsc(societyId, JoinRequestStatus.PENDING)
                .stream().map(JoinRequestResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<JoinRequestResponse> getForUser(Long userId) {
        return joinRequestRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(JoinRequestResponse::fromEntity).toList();
    }

    @Transactional
    public JoinRequestResponse approve(Long societyId, Long requestId) {
        JoinRequest request = findPending(societyId, requestId);

        AddMemberRequest add = new AddMemberRequest();
        add.setUserId(request.getUser().getId());
        add.setRole(SocietyRole.MEMBER);
        membershipService.addMember(societyId, add);

        request.setStatus(JoinRequestStatus.APPROVED);
        request.setDecidedAt(LocalDateTime.now());
        return JoinRequestResponse.fromEntity(joinRequestRepository.save(request));
    }

    @Transactional
    public JoinRequestResponse decline(Long societyId, Long requestId) {
        JoinRequest request = findPending(societyId, requestId);
        request.setStatus(JoinRequestStatus.DECLINED);
        request.setDecidedAt(LocalDateTime.now());
        return JoinRequestResponse.fromEntity(joinRequestRepository.save(request));
    }

    private JoinRequest findPending(Long societyId, Long requestId) {
        JoinRequest request = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (!request.getSociety().getId().equals(societyId)) {
            throw new ResourceNotFoundException("Request not found");
        }
        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new IllegalArgumentException("This request has already been handled.");
        }
        return request;
    }
}