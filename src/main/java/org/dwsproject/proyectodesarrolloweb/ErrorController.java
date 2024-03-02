package org.dwsproject.proyectodesarrolloweb;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice

public class ErrorController {
    // Test error 505
    @GetMapping("/error-test")
        public String throwError() {
        throw new InternalServerErrorException("Error de prueba");
    }
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e, HttpServletResponse res) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception", e);
        modelAndView.addObject("url", req.getRequestURL());
        modelAndView.addObject("status", res.getStatus());
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @ExceptionHandler(value = {org.springframework.web.servlet.NoHandlerFoundException.class})
        public ModelAndView handle404(HttpServletRequest req, Exception e, HttpServletResponse res) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("exception", e);
            modelAndView.addObject("url", req.getRequestURL());
            modelAndView.addObject("status", String.valueOf(404));
            modelAndView.setViewName("redirect:/error?error=404");
            return modelAndView;
}


    @ExceptionHandler(value = {InternalServerErrorException.class})
    public ModelAndView handle500(HttpServletRequest req, Exception e, HttpServletResponse res) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception", e);
        modelAndView.addObject("url", req.getRequestURL());
        modelAndView.addObject("status", String.valueOf(500));
        modelAndView.setViewName("error");
        return modelAndView;
    }
    public class InternalServerErrorException extends RuntimeException {
        public InternalServerErrorException(String message) {
            super(message);
        }
}

}