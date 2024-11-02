package com.yudiind.OnlineShop_Electronic.error;

import com.yudiind.OnlineShop_Electronic.error.exception.ConstrainViolationException;
import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(Exception e) {
        return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, "Access denied.", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<ApiError> handleInvalidArgumentException(Exception e){
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(Exception e){
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, e.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(ConstrainViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(Exception e){
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError){
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
