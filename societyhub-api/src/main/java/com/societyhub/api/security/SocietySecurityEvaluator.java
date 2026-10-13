package com.societyhub.api.security;

import com.societyhub.api.membership.Membership;
import com.societyhub.api.membership.MembershipRepository;
import com.societyhub.api.membership.SocietyRole;
import com.societyhub.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("societySecurity")
@RequiredArgsConstructor
public class SocietySecurityEvaluator {

    private final MembershipRepository membershipRepository;

    private static final List<SocietyRole> OFFICER_ROLES = List.of(
            SocietyRole.PRESIDENT,
            SocietyRole.VICE_PRESIDENT,
            SocietyRole.SECRETARY,
            SocietyRole.TREASURER
    );

    public boolean isSocietyOfficerOrAdmin(Long societyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User user)) {
            return false;
        }

        if (user.getRole().name().equals("COLLEGE_ADMIN")) {
            return true;
        }

        Optional<Membership> membershipOpt = membershipRepository.findByUserIdAndSocietyId(user.getId(), societyId);
        return membershipOpt.map(m -> OFFICER_ROLES.contains(m.getSocietyRole())).orElse(false);
    }

    public boolean isSocietyPresident(Long societyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User user)) {
            return false;
        }

        if (user.getRole().name().equals("COLLEGE_ADMIN")) {
            return true;
        }

        Optional<Membership> membershipOpt = membershipRepository.findByUserIdAndSocietyId(user.getId(), societyId);
        return membershipOpt.map(m -> m.getSocietyRole() == SocietyRole.PRESIDENT).orElse(false);
    }

    public boolean isSocietyMember(Long societyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User user)) {
            return false;
        }

        if (user.getRole().name().equals("COLLEGE_ADMIN")) {
            return true;
        }

        return membershipRepository.existsByUserIdAndSocietyId(user.getId(), societyId);
    }
}