package com.societyhub.api.membership;

import com.societyhub.api.common.exception.ResourceNotFoundException;
import com.societyhub.api.membership.dto.AddMemberRequest;
import com.societyhub.api.membership.dto.MembershipResponse;
import com.societyhub.api.society.Society;
import com.societyhub.api.society.SocietyRepository;
import com.societyhub.api.user.User;
import com.societyhub.api.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MembershipServiceTest {

    @Mock private MembershipRepository membershipRepository;
    @Mock private UserRepository userRepository;
    @Mock private SocietyRepository societyRepository;
    @InjectMocks private MembershipService service;

    private Society society;

    @BeforeEach
    void setUp() {
        society = mock(Society.class);
        when(society.getId()).thenReturn(1L);
        when(society.getName()).thenReturn("Coding Club");
        when(membershipRepository.save(any(Membership.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // Do not call this (or membership()) inside when(...).thenReturn(...): Mockito cannot
    // start a new stub while another one is unfinished. Assign the result to a variable first.
    private User user(long id) {
        User u = mock(User.class);
        when(u.getId()).thenReturn(id);
        return u;
    }

    private Membership membership(long userId, SocietyRole role) {
        return Membership.builder().user(user(userId)).society(society).societyRole(role).build();
    }

    private AddMemberRequest addRequest(String email, SocietyRole role) {
        AddMemberRequest r = new AddMemberRequest();
        r.setEmail(email);
        r.setRole(role);
        return r;
    }

    @Test
    void cannotRemoveTheOnlyPresident() {
        Membership president = membership(1, SocietyRole.PRESIDENT);
        when(membershipRepository.findByUserIdAndSocietyId(1L, 1L)).thenReturn(Optional.of(president));
        Membership member = membership(2, SocietyRole.MEMBER); // created before stubbing, not inside it
        when(membershipRepository.findBySocietyId(1L)).thenReturn(List.of(president, member));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.removeMember(1L, 1L));

        assertTrue(ex.getMessage().contains("at least one president"));
        verify(membershipRepository, never()).delete(any(Membership.class));
    }

    @Test
    void canRemoveAPresidentWhenAnotherOneExists() {
        Membership first = membership(1, SocietyRole.PRESIDENT);
        Membership second = membership(2, SocietyRole.PRESIDENT);
        when(membershipRepository.findByUserIdAndSocietyId(1L, 1L)).thenReturn(Optional.of(first));
        when(membershipRepository.findBySocietyId(1L)).thenReturn(List.of(first, second));

        service.removeMember(1L, 1L);

        verify(membershipRepository).delete(first);
    }

    @Test
    void cannotDemoteTheOnlyPresident() {
        Membership president = membership(1, SocietyRole.PRESIDENT);
        when(membershipRepository.findByUserIdAndSocietyId(1L, 1L)).thenReturn(Optional.of(president));
        when(membershipRepository.findBySocietyId(1L)).thenReturn(List.of(president));

        assertThrows(IllegalArgumentException.class, () -> service.updateMemberRole(1L, 1L, SocietyRole.SECRETARY));

        assertEquals(SocietyRole.PRESIDENT, president.getSocietyRole());
    }

    @Test
    void canDemoteAPresidentWhenAnotherOneExists() {
        Membership first = membership(1, SocietyRole.PRESIDENT);
        Membership second = membership(2, SocietyRole.PRESIDENT);
        when(membershipRepository.findByUserIdAndSocietyId(1L, 1L)).thenReturn(Optional.of(first));
        when(membershipRepository.findBySocietyId(1L)).thenReturn(List.of(first, second));

        MembershipResponse response = service.updateMemberRole(1L, 1L, SocietyRole.SECRETARY);

        assertEquals(SocietyRole.SECRETARY, response.getRole());
    }

    @Test
    void keepingThePresidentRoleIsAlwaysAllowed() {
        Membership president = membership(1, SocietyRole.PRESIDENT);
        when(membershipRepository.findByUserIdAndSocietyId(1L, 1L)).thenReturn(Optional.of(president));

        MembershipResponse response = service.updateMemberRole(1L, 1L, SocietyRole.PRESIDENT);

        assertEquals(SocietyRole.PRESIDENT, response.getRole());
    }

    @Test
    void removingANonMemberFails() {
        when(membershipRepository.findByUserIdAndSocietyId(5L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.removeMember(1L, 5L));
    }

    // ---- adding members ----

    @Test
    void addsAnExistingUserByEmail() {
        User person = user(3); // created before stubbing, not inside it
        when(userRepository.findByEmail("a@college.edu")).thenReturn(Optional.of(person));
        when(membershipRepository.existsByUserIdAndSocietyId(3L, 1L)).thenReturn(false);
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));

        MembershipResponse response = service.addMember(1L, addRequest("a@college.edu", SocietyRole.MEMBER));

        assertEquals(SocietyRole.MEMBER, response.getRole());
        verify(membershipRepository).save(any(Membership.class));
    }

    @Test
    void trimsWhitespaceAroundTheEmail() {
        User person = user(3); // created before stubbing, not inside it
        when(userRepository.findByEmail("a@college.edu")).thenReturn(Optional.of(person));
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));

        service.addMember(1L, addRequest("  a@college.edu  ", SocietyRole.MEMBER));

        verify(userRepository).findByEmail("a@college.edu");
    }

    @Test
    void tellsTheCallerWhenNoAccountExistsForTheEmail() {
        when(userRepository.findByEmail("nobody@college.edu")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> service.addMember(1L, addRequest("nobody@college.edu", SocietyRole.MEMBER)));

        assertTrue(ex.getMessage().contains("register first"));
    }

    @Test
    void refusesToAddSomeoneWhoIsAlreadyAMember() {
        User person = user(3); // created before stubbing, not inside it
        when(userRepository.findByEmail("a@college.edu")).thenReturn(Optional.of(person));
        when(membershipRepository.existsByUserIdAndSocietyId(3L, 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.addMember(1L, addRequest("a@college.edu", SocietyRole.MEMBER)));

        verify(membershipRepository, never()).save(any(Membership.class));
    }

    @Test
    void needsEitherAnEmailOrAUserId() {
        assertThrows(IllegalArgumentException.class, () -> service.addMember(1L, addRequest(null, SocietyRole.MEMBER)));
    }
}