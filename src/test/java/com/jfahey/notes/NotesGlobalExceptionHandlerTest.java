package com.jfahey.notes;

import com.jfahey.notes.model.api.NotesProblemDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

public class NotesGlobalExceptionHandlerTest {

    private static final Clock clock = Clock.systemDefaultZone();

    private NotesGlobalExceptionHandler notesGlobalExceptionHandler;

    private WebRequest request;

    @BeforeEach
    public void setUp() {
        notesGlobalExceptionHandler = new NotesGlobalExceptionHandler(clock);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContextPath("/notes/bad");
        request = new ServletWebRequest(mockRequest);
    }

    @Test
    @DisplayName("Test RuntimeException Handled Successfully")
    void givenRuntimeException_testHandleRuntimeException() {
        RuntimeException e = new RuntimeException("An exception");

        NotesProblemDetails response = notesGlobalExceptionHandler.handleRuntimeException(e, request);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getDetail()).contains("An error occurred while processing the request.");
        assertThat(response.getInstance()).isNotNull();
        assertThat(response.getInstance().getPath()).isEqualTo("/notes/bad");
    }
}
