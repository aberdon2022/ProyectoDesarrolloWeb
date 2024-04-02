package org.dwsproject.proyectodesarrolloweb.Controllers;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UnauthorizedAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.FieldError;
import java.util.stream.Collectors;

@ControllerAdvice // This applies to all controllers
public class GlobalExceptionHandler { // This class will handle all exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class) // This method will handle validation exceptions
    public String handleValidationExceptions(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes) {
        String errorMessage = ex.getBindingResult() // Get the error messages
                .getFieldErrors()                   // Get the field errors
                .stream()                           // Convert to stream
                .map(FieldError::getDefaultMessage) // Get the default message
                .collect(Collectors.joining(", ")); // Join the messages
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage); // Add the error message to the flash attributes
        return "redirect:/error/400"; // Redirect to your custom error page
    }

    @ExceptionHandler(UnauthorizedAccessException.class) // This method will handle unauthorized access exceptions
    public String handleUnauthorizedAccessException(UnauthorizedAccessException ex, RedirectAttributes redirectAttributes) {
        return "redirect:/error/401"; // Redirect to your custom error page
    }
}