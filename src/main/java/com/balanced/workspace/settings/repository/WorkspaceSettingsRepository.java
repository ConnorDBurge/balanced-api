package com.balanced.workspace.settings.repository;

import com.balanced.workspace.settings.entity.WorkspaceSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceSettingsRepository extends JpaRepository<WorkspaceSettings, UUID> {

    Optional<WorkspaceSettings> findByWorkspaceId(UUID workspaceId);
}
