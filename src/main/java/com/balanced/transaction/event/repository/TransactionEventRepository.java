package com.balanced.transaction.event.repository;

import com.balanced.transaction.event.entity.TransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionEventRepository extends JpaRepository<TransactionEvent, UUID> {

    List<TransactionEvent> findAllByTransactionIdAndWorkspaceIdOrderByPerformedAtDesc(UUID transactionId, UUID workspaceId);
}
