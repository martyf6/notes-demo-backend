package com.jfahey.notes.exception;

public class NoteNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Note not found";

    public NoteNotFoundException() {
        this(DEFAULT_MESSAGE);
    }

    public NoteNotFoundException(String message) {
        super(message);
    }
}
