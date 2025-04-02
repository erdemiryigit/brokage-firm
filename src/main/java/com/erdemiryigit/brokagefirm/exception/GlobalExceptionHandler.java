package com.erdemiryigit.brokagefirm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// todo loglamaya bak

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        log.error("Exception: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Internal Server Error");
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }

    // 401
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    private ProblemDetail handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), "Authentication credentials NOT found");
        problemDetail.setTitle("Authentication Credentials Not Found");
        return problemDetail;
    }

    // 403
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Access Denied");
        problemDetail.setTitle("Access Denied");
        return problemDetail;
    }

    // 500
    @ExceptionHandler(InterruptedException.class)
    private ProblemDetail handleInterruptedException() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), "Internal Server Error");
        problemDetail.setTitle("Interrupted Exception");
        return problemDetail;
    }

    // 404
    @ExceptionHandler(NoResourceFoundException.class)
    private ProblemDetail handleNoResourceFoundException(NoResourceFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Resource NOT found");
        problemDetail.setTitle("Resource Not Found");
        return problemDetail;
    }

    // 422
    @ExceptionHandler(OrderInterruptedException.class)
    private ProblemDetail handleOrderInterruptedException() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), "Order was interrupted try again later!");
        problemDetail.setTitle("Order Interrupted");
        return problemDetail;
    }

    @ExceptionHandler(OrderStatusException.class)
    private ProblemDetail handleOrderStatusException() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), "Order status is NOT valid!");
        problemDetail.setTitle("Order Status Error");
        return problemDetail;
    }

    @ExceptionHandler(CustomerAssetInsufficientException.class)
    private ProblemDetail handleCustomerAssetInsufficientException() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), "Customer does NOT have enough asset");
        problemDetail.setTitle("Customer Asset Insufficient");
        return problemDetail;
    }


    // 400
    @ExceptionHandler(AssetNotFoundException.class)
    private ProblemDetail handleAssetNotFound() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Asset does NOT exist!");
        problemDetail.setTitle("Not Found");
        return problemDetail;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    private ProblemDetail handleOrderNotFound() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Order does NOT exist!");
        problemDetail.setTitle("Order NOT Found");
        return problemDetail;
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    private ProblemDetail handleCustomerNotFound() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Customer does NOT exist!");
        problemDetail.setTitle("Customer NOT Found");
        return problemDetail;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    private ProblemDetail handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Missing request parameter: " + ex.getParameterName());
        problemDetail.setTitle("Missing Servlet Request Parameter");
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Malformed JSON request");
        problemDetail.setTitle("Bad Request");
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Validation error");
        problemDetail.setTitle("Validation Error");
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ProblemDetail handleIllegalArgumentException() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Illegal argument! Please check your request.");
        problemDetail.setTitle("Illegal Argument");
        return problemDetail;
    }
}