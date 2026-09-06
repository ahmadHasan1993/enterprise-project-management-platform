package com.ahmadhassan.projectmanagement.api;

import com.ahmadhassan.projectmanagement.identity.EmailAlreadyInUseException;
import com.ahmadhassan.projectmanagement.identity.InvalidPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmailAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleEmailAlreadyInUse(EmailAlreadyInUseException exception) {
        return new ApiErrorResponse(
                "EMAIL_ALREADY_IN_USE",
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidPassword(InvalidPasswordException exception) {
        return new ApiErrorResponse(
                "PASSWORD_INVALID",
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidation(MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (first, second) -> first
                ));

        return new ApiErrorResponse(
                "VALIDATION_FAILED",
                "Request validation failed",
                fieldErrors
        );
    }
}