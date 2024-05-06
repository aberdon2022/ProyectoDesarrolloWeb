package org.dwsproject.proyectodesarrolloweb.Controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (isApiRequest(request)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("errorCode", statusCode);

                Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
                if (errorMessage != null) {
                    errorResponse.put("errorMessage", errorMessage.toString());
                } else {
                    errorResponse.put("errorMessage", "Something went wrong");
                }
                return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
            } else {
                model.addAttribute("errorCode", statusCode);
                model.addAttribute("errorMessage", getErrorMessage(statusCode));
                return "Error";
            }
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String getErrorMessage(int errorCode) {
        return switch (errorCode) {
            case 403 -> "Forbidden";
            case 500 -> "Internal server error";
            case 404 -> "Page not found";
            case 400 -> "Bad request";
            case 401 -> "Unauthorized access";
            default -> "An error occurred";
        };
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && !userAgent.contains("Mozilla") && !userAgent.contains("Chrome") && !userAgent.contains("Safari");
    }
}