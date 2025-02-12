package com.jfahey.notes.exception;

public class InvalidAuthenticationTokenException extends RuntimeException {
    public InvalidAuthenticationTokenException() {
        super();
    }

    public InvalidAuthenticationTokenException(String message) {
        super(message);
    }
}
