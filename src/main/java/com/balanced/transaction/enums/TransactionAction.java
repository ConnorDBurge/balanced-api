package com.balanced.transaction.enums;

import com.balanced.common.event.DomainAction;

public enum TransactionAction implements DomainAction {
    CREATED,
    UPDATED,
    GROUPED,
    UNGROUPED,
    SPLIT,
    UNSPLIT;

    @Override
    public String domain() {
        return "transaction";
    }

    @Override
    public String action() {
        return name().toLowerCase();
    }
}
