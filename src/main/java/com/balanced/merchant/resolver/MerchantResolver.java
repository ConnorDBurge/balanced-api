package com.balanced.merchant.resolver;

import com.balanced.common.graphql.GraphQLContext;
import com.balanced.merchant.dto.CreateMerchantInput;
import com.balanced.merchant.dto.MerchantResponse;
import com.balanced.merchant.dto.UpdateMerchantInput;
import com.balanced.merchant.mapper.MerchantMapper;
import com.balanced.merchant.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MerchantResolver {

    private final MerchantService merchantService;
    private final MerchantMapper merchantMapper;

    @PreAuthorize("hasAuthority('READ')")
    @QueryMapping
    public List<MerchantResponse> merchants() {
        UUID workspaceId = GraphQLContext.workspaceId();
        return merchantMapper.toDtos(merchantService.listAllByWorkspaceId(workspaceId));
    }

    @PreAuthorize("hasAuthority('READ')")
    @QueryMapping
    public MerchantResponse merchant(@Argument UUID merchantId) {
        UUID workspaceId = GraphQLContext.workspaceId();
        return merchantMapper.toDto(merchantService.getMerchant(merchantId, workspaceId));
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @MutationMapping
    public MerchantResponse createMerchant(@Argument @Valid CreateMerchantInput input) {
        UUID workspaceId = GraphQLContext.workspaceId();
        return merchantMapper.toDto(merchantService.createMerchant(workspaceId, input));
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @MutationMapping
    public MerchantResponse updateMerchant(@Argument UUID merchantId, @Argument @Valid UpdateMerchantInput input) {
        UUID workspaceId = GraphQLContext.workspaceId();
        return merchantMapper.toDto(merchantService.updateMerchant(merchantId, workspaceId, input));
    }

    @PreAuthorize("hasAuthority('DELETE')")
    @MutationMapping
    public boolean deleteMerchant(@Argument UUID merchantId) {
        UUID workspaceId = GraphQLContext.workspaceId();
        merchantService.deleteMerchant(merchantId, workspaceId);
        return true;
    }
}
