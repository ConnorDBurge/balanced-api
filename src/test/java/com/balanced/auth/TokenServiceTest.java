package com.balanced.auth;

import com.balanced.auth.service.TokenService;
import com.balanced.auth.utils.JwtUtil;
import com.balanced.common.exception.ResourceNotFoundException;
import com.balanced.user.entity.User;
import com.balanced.user.repository.UserRepository;
import com.balanced.workspace.membership.entity.WorkspaceMembership;
import com.balanced.workspace.membership.enums.Role;
import com.balanced.workspace.membership.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock private JwtUtil jwtUtil;
    @Mock private MembershipService membershipService;
    @Mock private UserRepository userRepository;

    @InjectMocks private TokenService tokenService;

    private UUID userId;
    private UUID workspaceId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        workspaceId = UUID.randomUUID();
        testUser = User.builder()
                .id(userId)
                .email("connor@test.com")
                .givenName("Connor")
                .familyName("Burge")
                .isActive(true)
                .build();
    }

    @Test
    void mintToken_withMembership_includesRoles() {
        WorkspaceMembership membership = WorkspaceMembership.builder()
                .roles(Set.of(Role.OWNER, Role.WRITE, Role.DELETE, Role.READ))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(membershipService.findMembership(userId, workspaceId))
                .thenReturn(Optional.of(membership));
        when(jwtUtil.generateToken(eq(userId), eq(workspaceId), eq("connor@test.com"), anyList()))
                .thenReturn("test-token");

        String token = tokenService.mintToken(userId, workspaceId);

        assertThat(token).isEqualTo("test-token");
        verify(jwtUtil).generateToken(eq(userId), eq(workspaceId), eq("connor@test.com"), anyList());
    }

    @Test
    void mintToken_withoutMembership_defaultsToRead() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(membershipService.findMembership(userId, workspaceId))
                .thenReturn(Optional.empty());
        when(jwtUtil.generateToken(eq(userId), eq(workspaceId), eq("connor@test.com"), eq(List.of("READ"))))
                .thenReturn("test-token");

        String token = tokenService.mintToken(userId, workspaceId);

        assertThat(token).isEqualTo("test-token");
        verify(jwtUtil).generateToken(eq(userId), eq(workspaceId), eq("connor@test.com"), eq(List.of("READ")));
    }

    @Test
    void mintToken_userNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tokenService.mintToken(userId, workspaceId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void mintToken_usesEmailAsSubject() {
        testUser.setEmail("different@email.com");
        WorkspaceMembership membership = WorkspaceMembership.builder()
                .roles(Set.of(Role.READ))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(membershipService.findMembership(userId, workspaceId))
                .thenReturn(Optional.of(membership));
        when(jwtUtil.generateToken(eq(userId), eq(workspaceId), eq("different@email.com"), anyList()))
                .thenReturn("token");

        tokenService.mintToken(userId, workspaceId);

        verify(jwtUtil).generateToken(eq(userId), eq(workspaceId), eq("different@email.com"), anyList());
    }
}
