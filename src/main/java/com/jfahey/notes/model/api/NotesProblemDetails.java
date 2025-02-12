package com.jfahey.notes.model.api;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

/**
 * Custom {@link ProblemDetail} class.
 * This class is to be used as a representation for exceptions thrown by the API
 * with adherence to the specification outlined in RFC-9457 for HTTP responses.
 */
public class NotesProblemDetails extends ProblemDetail {

    private static final String TS_PROPERTY = "timestamp";
    private static final String ERRORS_PROPERTY = "errors";
    private static final URI DEFAULT_TYPE = URI.create("about:blank");

    public NotesProblemDetails(Throwable throwable, HttpStatusCode statusCode) {
        super(statusCode.value());
        this.setDetail(throwable.getMessage());
    }

    public NotesProblemDetails(Throwable throwable, HttpStatusCode statusCode, WebRequest request) {
        this(throwable, statusCode);
        this.setInstance(URI.create(request.getContextPath()));
    }

    public Object getTimestamp() {
        return this.getProperty(TS_PROPERTY);
    }

    public void setTimestamp(TemporalAccessor timestamp) {
        setProperty(TS_PROPERTY, timestamp);
    }

    public Object getErrors() {
        return getProperty(ERRORS_PROPERTY);
    }

    public void setErrors(List<? extends Serializable> errors) {
        setProperty(ERRORS_PROPERTY, errors);
    }

    private Object getProperty(String propertyName) {
        if (getProperties() == null) {
            return null;
        }
        return getProperties().get(propertyName);
    }

    /**
     * Create a default {@link NotesProblemDetails} instance.
     * @param throwable exception that caused the problem.
     * @param statusCode status code for the problem.
     * @param request requested resource that caused the problem.
     * @return A {@link NotesProblemDetails} instance  with default details.
     */
    public static NotesProblemDetails of(Throwable throwable, HttpStatusCode statusCode, WebRequest request) {
        NotesProblemDetails details = new NotesProblemDetails(throwable, statusCode, request);
        details.setType(DEFAULT_TYPE);
        details.setTimestamp(OffsetDateTime.now());
        return details;
    }

    /**
     * Create a new {@link NotesProblemDetails} instance.
     * @param throwable exception that caused the problem.
     * @param statusCode status code for the problem.
     * @param request requested resource that caused the problem.
     * @param title title for the problem.
     * @param detail detailed description for the problem.
     * @param timestamp timestamp for the problem's occurrence.
     * @return A {@link NotesProblemDetails} instance  with default details.
     */
    public static NotesProblemDetails of(Throwable throwable,
                                         HttpStatusCode statusCode,
                                         WebRequest request,
                                         String title,
                                         String detail,
                                         TemporalAccessor timestamp) {
        NotesProblemDetails details = new NotesProblemDetails(throwable, statusCode, request);
        details.setType(DEFAULT_TYPE);
        details.setTitle(title);
        details.setDetail(detail);
        details.setTimestamp(timestamp);
        return details;
    }
}
