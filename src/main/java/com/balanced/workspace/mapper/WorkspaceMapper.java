package com.balanced.workspace.mapper;

import com.balanced.workspace.dto.WorkspaceResponse;
import com.balanced.workspace.dto.WorkspaceAuthResponse;
import com.balanced.workspace.entity.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {

    WorkspaceResponse toDto(Workspace workspace);

    List<WorkspaceResponse> toDtos(List<Workspace> workspaces);

    @Mapping(target = "token", source = "token")
    WorkspaceAuthResponse toResponse(Workspace workspace, String token);
}
