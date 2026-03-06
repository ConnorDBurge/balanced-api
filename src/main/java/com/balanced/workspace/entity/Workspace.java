package com.balanced.workspace.entity;

import com.balanced.common.audit.BaseEntity;
import com.balanced.workspace.enums.WorkspaceStatus;
import com.balanced.workspace.membership.entity.WorkspaceMembership;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "workspaces")
@EqualsAndHashCode(callSuper = true)
public class Workspace extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "workspace")
    @Builder.Default
    private List<WorkspaceMembership> memberships = new ArrayList<>();
}
