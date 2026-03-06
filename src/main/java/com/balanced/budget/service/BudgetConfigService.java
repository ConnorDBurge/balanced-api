package com.balanced.budget.service;

import com.balanced.budget.dto.SetExpectedAmountInput;
import com.balanced.budget.dto.UpdateBudgetCategoryConfigInput;
import com.balanced.budget.entity.Budget;
import com.balanced.budget.entity.BudgetCategoryConfig;
import com.balanced.budget.entity.BudgetPeriodEntry;
import com.balanced.budget.enums.RolloverType;
import com.balanced.budget.repository.BudgetCategoryConfigRepository;
import com.balanced.budget.repository.BudgetPeriodEntryRepository;
import com.balanced.budget.util.BudgetPeriodCalculator;
import com.balanced.budget.util.PeriodRange;
import com.balanced.category.repository.CategoryRepository;
import com.balanced.category.service.CategoryService;
import com.balanced.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BudgetConfigService {

    private final BudgetCategoryConfigRepository configRepository;
    private final BudgetPeriodEntryRepository entryRepository;
    private final BudgetService budgetService;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    @Transactional
    public BudgetCategoryConfig updateConfig(UUID workspaceId, UUID budgetId, UUID categoryId,
                                             UpdateBudgetCategoryConfigInput dto) {
        categoryService.getCategory(categoryId, workspaceId);

        BudgetCategoryConfig config = configRepository.findByBudgetIdAndCategoryId(budgetId, categoryId)
                .orElseGet(() -> BudgetCategoryConfig.builder()
                        .workspaceId(workspaceId)
                        .budgetId(budgetId)
                        .categoryId(categoryId)
                        .build());

        config.setRolloverType(dto.getRolloverType());
        config = configRepository.save(config);

        log.info("Set rollover type {} for category {} in budget {}", dto.getRolloverType(), categoryId, budgetId);
        return config;
    }

    @Transactional
    public void setExpectedAmount(UUID workspaceId, UUID budgetId, UUID categoryId, LocalDate periodStart,
                                   SetExpectedAmountInput dto) {
        categoryService.getCategory(categoryId, workspaceId);

        if (categoryRepository.existsByParentId(categoryId)) {
            throw new BadRequestException("Cannot set expected amount on a parent category. Expected amounts roll up from children.");
        }

        Budget budget = budgetService.findBudget(workspaceId, budgetId);
        PeriodRange period = BudgetPeriodCalculator.computePeriod(budget, periodStart);
        if (!period.start().equals(periodStart)) {
            throw new BadRequestException(
                    "periodStart %s does not align with budget period configuration. Expected %s"
                            .formatted(periodStart, period.start()));
        }

        if (dto.getExpectedAmount() == null) {
            entryRepository.deleteByBudgetIdAndCategoryIdAndPeriodStart(budgetId, categoryId, periodStart);
            log.info("Cleared expected amount for category {} in period {} budget {}", categoryId, periodStart, budgetId);
            return;
        }

        BudgetPeriodEntry entry = entryRepository
                .findByBudgetIdAndCategoryIdAndPeriodStart(budgetId, categoryId, periodStart)
                .orElseGet(() -> BudgetPeriodEntry.builder()
                        .workspaceId(workspaceId)
                        .budgetId(budgetId)
                        .categoryId(categoryId)
                        .periodStart(periodStart)
                        .build());

        entry.setExpectedAmount(dto.getExpectedAmount());
        entryRepository.save(entry);

        log.info("Set expected amount {} for category {} in period {} budget {}",
                dto.getExpectedAmount(), categoryId, periodStart, budgetId);
    }

    @Transactional(readOnly = true)
    public RolloverType getRolloverType(UUID budgetId, UUID categoryId) {
        return configRepository.findByBudgetIdAndCategoryId(budgetId, categoryId)
                .map(BudgetCategoryConfig::getRolloverType)
                .orElse(RolloverType.NONE);
    }
}
