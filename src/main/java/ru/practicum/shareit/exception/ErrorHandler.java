package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(SubstanceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundCases(RuntimeException e) {
        log.error("404 {}", e.getMessage(), e);
        return new ErrorResponse("Object not found", e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmailCases(RuntimeException e) {
        log.error("409 {}", e.getMessage(), e);
        return new ErrorResponse("Conflict with email", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenToUser(RuntimeException e) {
        log.error("403 {}", e.getMessage(), e);
        return new ErrorResponse("This action is forbidden for user with this id", e.getMessage());
    }
}
