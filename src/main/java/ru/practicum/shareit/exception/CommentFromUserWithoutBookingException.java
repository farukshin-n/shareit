package ru.practicum.shareit.exception;

public class CommentFromUserWithoutBookingException extends RuntimeException {
    public CommentFromUserWithoutBookingException(String message) {
        super(message);
    }
}
