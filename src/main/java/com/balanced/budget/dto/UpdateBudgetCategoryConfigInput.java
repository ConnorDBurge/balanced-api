package com.balanced.budget.dto;

import com.balanced.budget.enums.RolloverType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBudgetCategoryConfigInput {

    private RolloverType rolloverType;
}
