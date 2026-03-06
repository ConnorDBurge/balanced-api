package com.balanced.category.dto;

import com.balanced.common.dto.BaseDto;
import com.balanced.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class CategoryResponse extends BaseDto {

    private UUID workspaceId;
    private UUID parentId;
    private String name;
    private String description;
    private Status status;
    private boolean income;
    private boolean excludeFromBudget;
    private boolean excludeFromTotals;
    private int displayOrder;

}
