package com.balanced.budget.repository;

import com.balanced.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findAllByWorkspaceId(UUID workspaceId);

    Optional<Budget> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    boolean existsByWorkspaceIdAndName(UUID workspaceId, String name);
}
