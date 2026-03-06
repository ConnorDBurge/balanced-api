package com.balanced.user.service;

import com.balanced.common.exception.ConflictException;
import com.balanced.common.exception.ResourceNotFoundException;
import com.balanced.user.dto.UpdateUserInput;
import com.balanced.user.entity.User;
import com.balanced.user.mapper.UserMapper;
import com.balanced.user.repository.UserRepository;
import com.balanced.workspace.dto.CreateWorkspaceInput;
import com.balanced.workspace.entity.Workspace;
import com.balanced.workspace.service.WorkspaceService;
import com.balanced.workspace.membership.service.MembershipService;
import com.balanced.workspace.membership.entity.WorkspaceMembership;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WorkspaceService workspaceService;
    private final MembershipService membershipService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public User updateUser(UUID userId, UpdateUserInput dto) {
        User user = getUser(userId);
        log.info("Updating user {}", user.getEmail());

        if (dto.getEmail() != null) {
            userRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(userId)) {
                    throw new ConflictException("Email already in use");
                }
            });
        }

        userMapper.updateEntity(dto, user);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = getUser(userId);
        log.info("Deleting user {}", user.getEmail());

        List<WorkspaceMembership> memberships = membershipService.listAllUserMemberships(userId);
        for (WorkspaceMembership membership : memberships) {
            membershipService.deleteMembership(userId, membership.getWorkspace().getId());
        }

        userRepository.delete(user);
        log.info("Successfully deleted user {}", user.getEmail());
    }

    /**
     * Creates the user and a default workspace if they don't already exist.
     */
    @Transactional
    public User bootstrapUser(String email, String givenName, String familyName) {
        log.debug("Bootstrapping user: email={}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            try {
                user = userRepository.save(User.builder()
                        .email(email)
                        .givenName(givenName != null ? givenName : email)
                        .familyName(familyName != null ? familyName : "")
                        .isActive(true)
                        .createdBy(email)
                        .build());
                log.debug("Created user {} for email {}", user.getId(), email);
            } catch (DataIntegrityViolationException e) {
                user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new ConflictException("User just created but not found"));
            }
        }

        if (user.getLastWorkspaceId() == null) {
            String workspaceName = String.format("%s's Workspace", user.getGivenName());
            Workspace workspace = workspaceService.provisionWorkspace(user.getId(),
                    CreateWorkspaceInput.builder().name(workspaceName).build());
            user.setLastWorkspaceId(workspace.getId());
            user = userRepository.save(user);
        }

        return user;
    }
}
