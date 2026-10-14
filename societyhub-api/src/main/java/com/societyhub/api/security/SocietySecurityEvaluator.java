package com.societyhub.api.security;

import com.societyhub.api.membership.Membership;
import com.societyhub.api.membership.MembershipRepository;
import com.societyhub.api.membership.SocietyRole;
import com.societyhub.api.user.User;
import com.societyhub.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("societySecurity")
@RequiredArgsConstructor
public class SocietySecurityEvaluator {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    private static final List<SocietyRole> OFFICER_ROLES = List.of(
            SocietyRole.PRESIDENT,
            SocietyRole.VICE_PRESIDENT,
            SocietyRole.SECRETARY,
            SocietyRole.TREASURER
    );

    public boolean isSocietyOfficerOrAdmin(Long societyId) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }

        // College Admin global override
        if (user.getRole() != null && user.getRole().name().equals("COLLEGE_ADMIN")) {
            return true;
        }

        Optional<Membership> membershipOpt = membershipRepository.findByUserIdAndSocietyId(user.getId(), societyId);
        return membershipOpt.map(m -> OFFICER_ROLES.contains(m.getSocietyRole())).orElse(false);
    }

    public boolean isSocietyPresident(Long societyId) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }

        if (user.getRole() != null && user.getRole().name().equals("COLLEGE_ADMIN")) {
            return true;
        }

        Optional<Membership> membershipOpt = membershipRepository.findByUserIdAndSocietyId(user.getId(), societyId);
        return membershipOpt.map(m -> m.getSocietyRole() == SocietyRole.PRESIDENT).orElse(false);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        String email = null;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        }

        if (email == null) {
            return null;
        }

        return userRepository.findByEmail(email).orElse(null);
    }
}