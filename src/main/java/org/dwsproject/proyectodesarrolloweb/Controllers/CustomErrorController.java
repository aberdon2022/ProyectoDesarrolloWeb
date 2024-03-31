package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CustomErrorController implements ErrorController {
    @GetMapping("/error/{errorCode}")
    public String handleCustomError(@PathVariable int errorCode, Model model) {
        String errorMessage = switch (errorCode) {
            case 403 -> "Forbidden";
            case 500 -> "Internal server error";
            case 404 -> "Page not found";
            case 400 -> "Bad request";
            case 401 -> "Unauthorized access";
            default -> "An error occurred";
        };
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", errorMessage);
        return "Error";
    }
}