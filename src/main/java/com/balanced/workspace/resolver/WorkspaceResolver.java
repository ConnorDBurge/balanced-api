package com.balanced.workspace.resolver;

import com.balanced.auth.service.TokenService;
import com.balanced.common.graphql.GraphQLContext;
import com.balanced.workspace.dto.CreateWorkspaceInput;
import com.balanced.workspace.dto.SwitchWorkspaceInput;
import com.balanced.workspace.dto.UpdateWorkspaceInput;
import com.balanced.workspace.dto.WorkspaceAuthResponse;
import com.balanced.workspace.dto.WorkspaceResponse;
import com.balanced.workspace.entity.Workspace;
import com.balanced.workspace.mapper.WorkspaceMapper;
import com.balanced.workspace.membership.dto.CreateMembershipInput;
import com.balanced.workspace.membership.dto.UpdateMembershipInput;
import com.balanced.workspace.membership.dto.WorkspaceMembershipResponse;
import com.balanced.workspace.membership.entity.WorkspaceMembership;
import com.balanced.workspace.membership.mapper.MembershipMapper;
import com.balanced.workspace.membership.service.MembershipService;
import com.balanced.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WorkspaceResolver {

    private final WorkspaceService workspaceService;
    private final MembershipService membershipService;
    private final TokenService tokenService;
    private final WorkspaceMapper workspaceMapper;
    private final MembershipMapper membershipMapper;

    @PreAuthorize("hasAuthority('READ')")
    @QueryMapping
    public List<WorkspaceResponse> workspaces() {
        UUID userId = GraphQLContext.userId();
        return workspaceMapper.toDtos(workspaceService.listAllByUserId(userId));
    }

    @PreAuthorize("hasAuthority('READ')")
    @QueryMapping
    public WorkspaceResponse currentWorkspace() {
        UUID workspaceId = GraphQLContext.workspaceId();
        return workspaceMapper.toDto(workspaceService.getWorkspace(workspaceId));
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @MutationMapping
    public WorkspaceAuthResponse provisionWorkspace(@Argument @Valid CreateWorkspaceInput input) {
        UUID userId = GraphQLContext.userId();
        Workspace workspace = workspaceService.provisionWorkspace(userId, input);
        Workspace switched = workspaceService.switchWorkspace(userId, workspace.getId());
        String token = tokenService.mintToken(userId, switched.getId());
        return workspaceMapper.toResponse(switched, token);
    }

    @PreAuthorize("hasAuthority('READ')")
    @MutationMapping
    public WorkspaceAuthResponse switchWorkspace(@Argument @Valid SwitchWorkspaceInput input) {
        UUID userId = GraphQLContext.userId();
        Workspace workspace = workspaceService.switchWorkspace(userId, input.getWorkspaceId());
        String token = tokenService.mintToken(userId, workspace.getId());
        return workspaceMapper.toResponse(workspace, token);
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @MutationMapping
    public WorkspaceResponse updateCurrentWorkspace(@Argument @Valid UpdateWorkspaceInput input) {
        UUID workspaceId = GraphQLContext.workspaceId();
        UUID userId = GraphQLContext.userId();
        return workspaceMapper.toDto(workspaceService.updateWorkspace(workspaceId, userId, input));
    }

    @PreAuthorize("hasAuthority('READ')")
    @QueryMapping
    public List<WorkspaceMembershipResponse> workspaceMembers() {
        UUID workspaceId = GraphQLContext.workspaceId();
        return membershipMapper.toDtos(workspaceService.listAllMembers(workspaceId));
    }

    @PreAuthorize("hasAuthority('READ')")
    @QueryMapping
    public WorkspaceMembershipResponse workspaceMember(@Argument UUID userId) {
        UUID workspaceId = GraphQLContext.workspaceId();
        WorkspaceMembership membership = membershipService.findMembership(userId, workspaceId)
                .orElseThrow(() -> new com.balanced.common.exception.ResourceNotFoundException("Member not found"));
        return membershipMapper.toDto(membership);
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @MutationMapping
    public WorkspaceMembershipResponse addWorkspaceMember(@Argument @Valid CreateMembershipInput input) {
        UUID workspaceId = GraphQLContext.workspaceId();
        WorkspaceMembership membership = membershipService
                .createMembership(input.getUserId(), workspaceId, input.getRoles());
        return membershipMapper.toDto(membership);
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @MutationMapping
    public WorkspaceMembershipResponse updateWorkspaceMember(@Argument UUID userId,
                                                             @Argument @Valid UpdateMembershipInput input) {
        UUID workspaceId = GraphQLContext.workspaceId();
        return membershipMapper.toDto(membershipService.updateMembership(userId, workspaceId, input));
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @MutationMapping
    public boolean removeWorkspaceMember(@Argument UUID userId) {
        UUID workspaceId = GraphQLContext.workspaceId();
        membershipService.deleteMembership(userId, workspaceId);
        return true;
    }
}
