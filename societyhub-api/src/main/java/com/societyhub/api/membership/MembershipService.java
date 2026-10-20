package com.societyhub.api.membership;

import com.societyhub.api.common.exception.ResourceNotFoundException;
import com.societyhub.api.membership.dto.AddMemberRequest;
import com.societyhub.api.membership.dto.MembershipResponse;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import com.societyhub.api.user.User;
import com.societyhub.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final SocietyRepository societyRepository;

    @Transactional
    public MembershipResponse addMember(Long societyId, AddMemberRequest request) {
        User user = resolveUser(request);

        if (membershipRepository.existsByUserIdAndSocietyId(user.getId(), societyId)) {
            throw new IllegalArgumentException("This person is already a member of the society.");
        }

        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found"));

        Membership membership = Membership.builder()
                .user(user)
                .society(society)
                .societyRole(request.getRole())
                .build();

        return mapToResponse(membershipRepository.save(membership));
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> getSocietyMembers(Long societyId) {
        return membershipRepository.findBySocietyId(societyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public MembershipResponse updateMemberRole(Long societyId, Long userId, SocietyRole newRole) {
        Membership membership = find(societyId, userId);
        ensureNotLastPresident(societyId, membership, newRole);
        membership.setSocietyRole(newRole);
        return mapToResponse(membershipRepository.save(membership));
    }

    @Transactional
    public void removeMember(Long societyId, Long userId) {
        Membership membership = find(societyId, userId);
        ensureNotLastPresident(societyId, membership, null);
        membershipRepository.delete(membership);
    }

    private Membership find(Long societyId, Long userId) {
        return membershipRepository.findByUserIdAndSocietyId(userId, societyId)
                .orElseThrow(() -> new ResourceNotFoundException("That person is not a member of this society"));
    }

    private User resolveUser(AddMemberRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = request.getEmail().trim();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No account found for " + email + ". They need to register first."));
        }
        if (request.getUserId() != null) {
            return userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        throw new IllegalArgumentException("Provide the member's email.");
    }
    private void ensureNotLastPresident(Long societyId, Membership target, SocietyRole newRole) {
        if (target.getSocietyRole() == SocietyRole.PRESIDENT && newRole != SocietyRole.PRESIDENT) {
            long presidents = membershipRepository.findBySocietyId(societyId).stream()
                    .filter(m -> m.getSocietyRole() == SocietyRole.PRESIDENT)
                    .count();
            if (presidents <= 1) {
                throw new IllegalArgumentException("A society must have at least one president. Make someone else president first.");
            }
        }
    }

    private MembershipResponse mapToResponse(Membership membership) {
        return MembershipResponse.builder()
                .id(membership.getId())
                .userId(membership.getUser().getId())
                .userEmail(membership.getUser().getEmail())
                .userFullName(membership.getUser().getFullName())
                .societyId(membership.getSociety().getId())
                .societyName(membership.getSociety().getName())
                .role(membership.getSocietyRole())
                .joinedAt(membership.getJoinedAt())
                .build();
    }
}