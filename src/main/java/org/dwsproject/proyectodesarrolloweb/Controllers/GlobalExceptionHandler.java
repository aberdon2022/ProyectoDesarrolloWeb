package org.dwsproject.proyectodesarrolloweb.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.dwsproject.proyectodesarrolloweb.Exceptions.FriendException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UnauthorizedAccessException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class) // This method will handle the MethodArgumentNotValidException exceptions
    public String handleValidationExceptions(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes) {
        String errorMessage = ex.getBindingResult() // Get the error messages
                .getFieldErrors()                   // Get the field errors
                .stream()                           // Convert to stream
                .map(FieldError::getDefaultMessage) // Get the default message
                .collect(Collectors.joining(", ")); // Join the messages
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/error/400";
    }

    @ExceptionHandler(UnauthorizedAccessException.class) // This method will handle UnauthorizedAccessException exceptions
    public Object handleUnauthorizedAccessException(UnauthorizedAccessException ex, HttpServletRequest request,RedirectAttributes redirectAttributes) {
        String acceptHeader = request.getHeader("Accept"); // Get the accept header

        if (acceptHeader != null && acceptHeader.contains("text/html")) { // If the accept header contains text/html
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage()); // Add the error message to the flash attributes
            return "redirect:/error/401";
        } else {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED); // Return a ResponseEntity with the exception message for the API
        }
    }

    @ExceptionHandler(UserAlreadyExistsException.class) // This method will handle user already exists exceptions
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // Return a ResponseEntity with the exception message for the API
    }

    @ExceptionHandler(FriendException.class) // This method will handle friend not found exceptions
    public ResponseEntity<String> handleFriendNotFoundException(FriendException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // Return a ResponseEntity with the exception message for the API
    }
}