package com.balanced.common.graphql;

import com.balanced.common.exception.AccessDeniedException;
import com.balanced.common.exception.BadRequestException;
import com.balanced.common.exception.ConflictException;
import com.balanced.common.exception.ResourceNotFoundException;
import graphql.GraphQLError;
import graphql.ErrorClassification;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.stream.Collectors;

@ControllerAdvice
public class GraphQLExceptionHandler {

    private enum BalancedErrorType implements ErrorClassification {
        BAD_REQUEST, NOT_FOUND, FORBIDDEN, CONFLICT, VALIDATION_ERROR
    }

    @GraphQlExceptionHandler
    public GraphQLError handleBadRequest(BadRequestException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .errorType(BalancedErrorType.BAD_REQUEST)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleNotFound(ResourceNotFoundException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .errorType(BalancedErrorType.NOT_FOUND)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleAccessDenied(AccessDeniedException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .errorType(BalancedErrorType.FORBIDDEN)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleConflict(ConflictException ex) {
        return GraphQLError.newError()
                .message(ex.getMessage())
                .errorType(BalancedErrorType.CONFLICT)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return GraphQLError.newError()
                .message(message)
                .errorType(BalancedErrorType.VALIDATION_ERROR)
                .build();
    }
}
