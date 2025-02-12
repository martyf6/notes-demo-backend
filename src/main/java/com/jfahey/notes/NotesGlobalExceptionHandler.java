package com.jfahey.notes;

import com.jfahey.notes.exception.InvalidAuthenticationTokenException;
import com.jfahey.notes.exception.NoteNotFoundException;
import com.jfahey.notes.model.api.NotesProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class NotesGlobalExceptionHandler {

    private static final String DEFAULT_PROBLEM_TITLE = "Internal server error";
    private static final String DEFAULT_PROBLEM_DETAIL = "An error occurred while processing the request.";

    private final Clock clock;

    public NotesGlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(NoteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NotesProblemDetails handleNoteNotFoundException(NoteNotFoundException e, WebRequest request) {
        return NotesProblemDetails.of(e,
                HttpStatus.NOT_FOUND,
                request,
                "Note not found",
                "The requested note could not be found.",
                OffsetDateTime.now(clock));
    }

    @ExceptionHandler(InvalidAuthenticationTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public NotesProblemDetails handleInvalidAuthenticationTokenException(InvalidAuthenticationTokenException e, WebRequest request) {
        return NotesProblemDetails.of(e,
                HttpStatus.UNAUTHORIZED,
                request,
                "Invalid authentication token",
                e.getMessage(),
                OffsetDateTime.now(clock));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NotesProblemDetails handleValidationErrors(MethodArgumentNotValidException e, WebRequest request) {
        List<String> errors = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();

        NotesProblemDetails problemDetails = NotesProblemDetails.of(e,
                HttpStatus.BAD_REQUEST,
                request,
                "Validation Error(s)",
                "An invalid request was received.",
                OffsetDateTime.now(clock));
        problemDetails.setErrors(errors);
        return problemDetails;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public NotesProblemDetails handleRuntimeException(RuntimeException e, WebRequest request) {
       return NotesProblemDetails.of(e,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                DEFAULT_PROBLEM_TITLE,
                DEFAULT_PROBLEM_DETAIL,
                OffsetDateTime.now(clock));
    }
}
