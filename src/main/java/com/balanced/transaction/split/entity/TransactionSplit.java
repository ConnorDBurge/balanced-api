package com.balanced.transaction.split.entity;

import com.balanced.account.enums.CurrencyCode;
import com.balanced.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "transaction_splits")
public class TransactionSplit extends BaseEntity {

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_code", nullable = false)
    private CurrencyCode currencyCode;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;
}
