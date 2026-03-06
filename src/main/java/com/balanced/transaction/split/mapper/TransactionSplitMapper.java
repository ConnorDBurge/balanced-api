package com.balanced.transaction.split.mapper;

import com.balanced.transaction.entity.Transaction;
import com.balanced.transaction.split.dto.TransactionSplitResponse;
import com.balanced.transaction.split.entity.TransactionSplit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TransactionSplitMapper {

    @Mapping(target = "transactionIds", ignore = true)
    TransactionSplitResponse toDto(TransactionSplit split);

    default TransactionSplitResponse toDto(TransactionSplit split, List<Transaction> transactions) {
        TransactionSplitResponse dto = toDto(split);
        return dto.toBuilder()
                .transactionIds(transactions.stream()
                        .map(Transaction::getId)
                        .collect(Collectors.toSet()))
                .build();
    }
}
