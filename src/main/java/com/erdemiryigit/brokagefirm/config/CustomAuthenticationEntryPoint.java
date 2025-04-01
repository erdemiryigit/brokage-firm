package com.erdemiryigit.brokagefirm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/problem+json");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(401),
                authException instanceof BadCredentialsException
                        ? "You've entered invalid credentials!"
                        : "Failed to authenticate!");

        problemDetail.setTitle(authException instanceof BadCredentialsException
                ? "Bad Credentials"
                : "Authentication Error");

        problemDetail.setType(ProblemDetail.forStatus(HttpStatusCode.valueOf(401)).getType());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", System.currentTimeMillis());

        response.getWriter().write(new ObjectMapper().writeValueAsString(problemDetail));
    }
}