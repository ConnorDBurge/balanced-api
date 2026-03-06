package com.balanced.user.resolver;

import com.balanced.common.graphql.GraphQLContext;
import com.balanced.user.dto.UpdateUserInput;
import com.balanced.user.dto.UserResponse;
import com.balanced.user.mapper.UserMapper;
import com.balanced.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserResolver {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasAuthority('READ')")
    @QueryMapping
    public UserResponse self() {
        UUID userId = GraphQLContext.userId();
        return userMapper.toDto(userService.getUser(userId));
    }

    @PreAuthorize("hasAuthority('WRITE')")
    @MutationMapping
    public UserResponse updateSelf(@Argument @Valid UpdateUserInput input) {
        UUID userId = GraphQLContext.userId();
        return userMapper.toDto(userService.updateUser(userId, input));
    }

    @PreAuthorize("hasAuthority('DELETE')")
    @MutationMapping
    public boolean deleteSelf() {
        UUID userId = GraphQLContext.userId();
        userService.deleteUser(userId);
        return true;
    }
}
