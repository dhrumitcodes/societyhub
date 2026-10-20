package com.societyhub.api.dashboard;
import com.societyhub.api.common.exception.ResourceNotFoundException;
import com.societyhub.api.dashboard.dto.DashboardResponse;
import com.societyhub.api.event.EventService;
import com.societyhub.api.event.dto.EventResponse;
import com.societyhub.api.membership.MembershipRepository;
import com.societyhub.api.membership.MembershipService;
import com.societyhub.api.membership.dto.MembershipResponse;
import com.societyhub.api.security.SocietySecurityEvaluator;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import com.societyhub.api.user.User;
import com.societyhub.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MembershipService membershipService;
    private final MembershipRepository membershipRepository;
    private final EventService eventService;
    private final SocietyRepository societyRepository;
    private final UserRepository userRepository;
    private final SocietySecurityEvaluator societySecurity;

    @GetMapping("/societies/{societyId}")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable Long societyId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var membership = membershipRepository.findByUserIdAndSocietyId(user.getId(), societyId);
        boolean isAdmin = user.getRole() != null && user.getRole().name().equals("COLLEGE_ADMIN");
        if (membership.isEmpty() && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this society");
        }

        Society society = societyRepository.findById(societyId)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found"));

        List<MembershipResponse> members = membershipService.getSocietyMembers(societyId);
        Map<String, Long> byRole = members.stream().collect(Collectors.groupingBy(
                m -> m.getRole().name(), LinkedHashMap::new, Collectors.counting()));

        Page<EventResponse> upcoming = eventService.getFilteredEvents(
                societyId, null, null, true, PageRequest.of(0, 5, Sort.by("startTime")));

        return ResponseEntity.ok(DashboardResponse.builder()
                .societyId(societyId)
                .societyName(society.getName())
                .myRole(membership.map(m -> m.getSocietyRole()).orElse(null))
                .canManage(societySecurity.isSocietyOfficerOrAdmin(societyId))
                .memberCount(members.size())
                .upcomingEventCount(upcoming.getTotalElements())
                .membersByRole(byRole)
                .upcomingEvents(upcoming.getContent())
                .build());
    }
}