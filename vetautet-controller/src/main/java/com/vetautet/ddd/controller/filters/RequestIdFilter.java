package com.vetautet.ddd.controller.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Order(1)
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "x-request-id";
    private static final String COLLECTION_ID = "COLLECTION_ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Generate a unique request ID
            addRequestId(request, response);

            // Continue the filter chain
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(COLLECTION_ID);
        }
    }

    private void addRequestId(HttpServletRequest request, HttpServletResponse response) {
        // Generate a unique request ID
        String requestId = request.getHeader(COLLECTION_ID);
        String requestIdService = getValueUUID();

        if (requestId == null) {
            // Add the request ID to the response header
            requestId = requestIdService;
        } else {
            requestId = requestId + " " + requestIdService;
        }

        MDC.put(COLLECTION_ID, requestIdService);

        response.addHeader(REQUEST_ID, requestId);
    }

    private String getValueUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }
}
