package com.balanced.workspace.settings.mapper;

import com.balanced.workspace.settings.dto.WorkspaceSettingsDto;
import com.balanced.workspace.settings.entity.WorkspaceSettings;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceSettingsMapper {

    WorkspaceSettingsDto toDto(WorkspaceSettings settings);
}
