package com.jobconnect.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles IllegalArgumentException from registration, etc.
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req, Model model) {

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                req.getRequestURI()
        );

        model.addAttribute("timestamp", apiError.getTimestamp());
        model.addAttribute("status", apiError.getStatus());
        model.addAttribute("message", apiError.getMessage());
        model.addAttribute("path", apiError.getPath());

        return "error"; // loads error.html
    }

    // Handles all other unexpected errors
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, HttpServletRequest req, Model model) {

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                req.getRequestURI()
        );

        model.addAttribute("timestamp", apiError.getTimestamp());
        model.addAttribute("status", apiError.getStatus());
        model.addAttribute("message", apiError.getMessage());
        model.addAttribute("path", apiError.getPath());

        return "error";
    }
}
