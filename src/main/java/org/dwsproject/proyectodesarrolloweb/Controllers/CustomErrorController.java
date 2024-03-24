package org.dwsproject.proyectodesarrolloweb.Controllers;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CustomErrorController implements ErrorController {
    @GetMapping("/error/{errorCode}")
    public String handleCustomError(@PathVariable int errorCode, Model model) {
        String errorMessage;
        switch (errorCode) {
            case 403:
                errorMessage = "Forbidden";
                break;
            case 500:
                errorMessage = "Internal server error";
                break;
            case 404:
                errorMessage = "Page not found";
                break;
            case 400:
                errorMessage = "Bad request";
                break;
            case 401:
                errorMessage = "Unauthorized access";
                break;
            default:
                errorMessage = "An error occurred";
                break;
        }
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", errorMessage);
        return "Error";
    }

    public String getErrorPath() {
        return "/error";
    }
}