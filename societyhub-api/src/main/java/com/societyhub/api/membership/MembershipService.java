package com.societyhub.api.membership;

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
        if (membershipRepository.existsByUserIdAndSocietyId(request.getUserId(), societyId)) {
            throw new IllegalArgumentException("User is already a member of this society.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new RuntimeException("Society not found with id: " + societyId));

        Membership membership = Membership.builder()
                .user(user)
                .society(society)
                .societyRole(request.getRole())
                .build();

        return mapToResponse(membershipRepository.save(membership));
    }

    public List<MembershipResponse> getSocietyMembers(Long societyId) {
        return membershipRepository.findBySocietyId(societyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public MembershipResponse updateMemberRole(Long societyId, Long userId, SocietyRole newRole) {
        Membership membership = membershipRepository.findByUserIdAndSocietyId(userId, societyId)
                .orElseThrow(() -> new RuntimeException("Membership not found for user in this society."));

        membership.setSocietyRole(newRole);
        return mapToResponse(membershipRepository.save(membership));
    }

    @Transactional
    public void removeMember(Long societyId, Long userId) {
        Membership membership = membershipRepository.findByUserIdAndSocietyId(userId, societyId)
                .orElseThrow(() -> new RuntimeException("Membership not found for user in this society."));

        membershipRepository.delete(membership);
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