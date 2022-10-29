package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({NotAvailableException.class, IllegalArgumentException.class,
            IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailableCases(RuntimeException e) {
        log.error("400 {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentMismatchCase(MethodArgumentTypeMismatchException e) {
        log.error("400 {}", e.getMessage(), e);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler({SubstanceNotFoundException.class, ForbiddenException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundCases(RuntimeException e) {
        log.error("404 {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}
