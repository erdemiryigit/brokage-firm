package com.erdemiryigit.brokagefirm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        logException(ex);

        if (ex instanceof AccessDeniedException) {
            return handleAccessDeniedException((AccessDeniedException) ex);
        } else if (ex instanceof AuthenticationCredentialsNotFoundException) {
            return handleAuthenticationCredentialsNotFoundException((AuthenticationCredentialsNotFoundException) ex);
        } else if (ex instanceof HttpMessageNotReadableException) {
            return handleHttpMessageNotReadableException((HttpMessageNotReadableException) ex);
        } else if (ex instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValidException((MethodArgumentNotValidException) ex);
        } else if (ex instanceof IllegalArgumentException) {
            return handleIllegalArgumentException((IllegalArgumentException) ex, request);
        } else if (ex instanceof MissingServletRequestParameterException) {
            return handleMissingServletRequestParameterException((MissingServletRequestParameterException) ex, request);
        } else if (ex instanceof InterruptedException) {
            return handleInterruptedException((InterruptedException) ex);
        } else if (ex instanceof UserNotFoundException) {
            return handleUserNotFoundException((UserNotFoundException) ex);
        } else {
            return handleGlobalException(ex, request);
        }
    }

    private void logException(Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);
    }

    private ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        String message = "You are not authorized to access this resource. If you believe this is an error, please contact support.";
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<?> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        String message = "Authentication is required to access this resource. Please log in and try again.";
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Your message has errors. Please check it. " + ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = "Validation failed for one or more arguments: " + ex.getBindingResult().toString();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String message = "Invalid argument: " + ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> handleInterruptedException(InterruptedException ex) {
        String message = "The request was interrupted! Please try again later.";
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, WebRequest request) {
        String message = "Missing servlet request parameter: " + ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        String message = "An unexpected error occurred: " + ex.getMessage();
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}