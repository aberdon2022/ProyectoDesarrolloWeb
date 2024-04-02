package org.dwsproject.proyectodesarrolloweb.Controllers;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            String token = authHeader;
            User user = userService.findUserByToken(token);

            //Verify if the token is from the user
            String requestUsername= request.getParameter("username");

            if (user != null && user.getUsername().equals(requestUsername)) {
                //If the token is valid, the user is authenticated and the request is allowed to continue
                filterChain.doFilter(request, response);
            } else {
                //If the token is invalid, the user is not authenticated and the request is rejected
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            //If the token is not present, the user is not authenticated and the request is rejected
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
