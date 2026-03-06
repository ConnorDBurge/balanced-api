package com.balanced.workspace.settings.dto;

import com.balanced.account.enums.CurrencyCode;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWorkspaceSettingsDto {

    private CurrencyCode defaultCurrencyCode;

    @Size(max = 50, message = "Timezone must be at most 50 characters")
    private String timezone;
}
