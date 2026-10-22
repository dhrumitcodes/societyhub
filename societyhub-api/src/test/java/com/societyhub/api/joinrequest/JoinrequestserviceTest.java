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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JoinRequestServiceTest {

    @Mock private JoinRequestRepository joinRequestRepository;
    @Mock private SocietyRepository societyRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private MembershipService membershipService;
    @InjectMocks private JoinRequestService service;

    private User user;
    private Society society;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        society = mock(Society.class);
        when(society.getId()).thenReturn(1L);
        when(society.getName()).thenReturn("Coding Club");
        when(joinRequestRepository.save(any(JoinRequest.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private JoinRequest pendingRequest(Society requestSociety) {
        return JoinRequest.builder().id(5L).user(user).society(requestSociety).build();
    }

    // ---- creating a request ----

    @Test
    void createsAPendingRequestWithATrimmedMessage() {
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));

        JoinRequestResponse response = service.create(1L, user, "  I love coding  ");

        Assertions.assertEquals(JoinRequestStatus.PENDING, response.getStatus());
        assertEquals("I love coding", response.getMessage());
    }

    @Test
    void storesNoMessageWhenItIsBlank() {
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));

        JoinRequestResponse response = service.create(1L, user, "   ");

        assertNull(response.getMessage());
    }

    @Test
    void membersCannotRequestToJoinAgain() {
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));
        when(membershipRepository.existsByUserIdAndSocietyId(7L, 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(1L, user, null));

        verify(joinRequestRepository, never()).save(any(JoinRequest.class));
    }

    @Test
    void cannotHaveTwoPendingRequestsForOneSociety() {
        when(societyRepository.findById(1L)).thenReturn(Optional.of(society));
        when(joinRequestRepository.existsByUserIdAndSocietyIdAndStatus(7L, 1L, JoinRequestStatus.PENDING)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(1L, user, null));

        verify(joinRequestRepository, never()).save(any(JoinRequest.class));
    }

    @Test
    void failsForAnUnknownSociety() {
        when(societyRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(9L, user, null));
    }

    // ---- approving and declining ----

    @Test
    void approvingAddsThePersonAsAPlainMember() {
        JoinRequest request = pendingRequest(society);
        when(joinRequestRepository.findById(5L)).thenReturn(Optional.of(request));

        service.approve(1L, 5L);

        ArgumentCaptor<AddMemberRequest> captor = ArgumentCaptor.forClass(AddMemberRequest.class);
        verify(membershipService).addMember(eq(1L), captor.capture());
        assertEquals(7L, captor.getValue().getUserId());
        assertEquals(SocietyRole.MEMBER, captor.getValue().getRole());
        assertEquals(JoinRequestStatus.APPROVED, request.getStatus());
        assertNotNull(request.getDecidedAt());
    }

    @Test
    void decliningDoesNotAddAMembership() {
        JoinRequest request = pendingRequest(society);
        when(joinRequestRepository.findById(5L)).thenReturn(Optional.of(request));

        service.decline(1L, 5L);

        assertEquals(JoinRequestStatus.DECLINED, request.getStatus());
        assertNotNull(request.getDecidedAt());
        verify(membershipService, never()).addMember(any(), any());
    }

    @Test
    void anOfficerCannotHandleARequestForAnotherSociety() {
        Society other = mock(Society.class);
        when(other.getId()).thenReturn(2L);
        when(joinRequestRepository.findById(5L)).thenReturn(Optional.of(pendingRequest(other)));

        assertThrows(ResourceNotFoundException.class, () -> service.approve(1L, 5L));

        verify(membershipService, never()).addMember(any(), any());
    }

    @Test
    void aRequestThatWasAlreadyHandledCannotBeHandledAgain() {
        JoinRequest request = pendingRequest(society);
        request.setStatus(JoinRequestStatus.DECLINED);
        when(joinRequestRepository.findById(5L)).thenReturn(Optional.of(request));

        assertThrows(IllegalArgumentException.class, () -> service.approve(1L, 5L));

        verify(membershipService, never()).addMember(any(), any());
    }

    @Test
    void anUnknownRequestIsNotFound() {
        when(joinRequestRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.decline(1L, 404L));
    }
}