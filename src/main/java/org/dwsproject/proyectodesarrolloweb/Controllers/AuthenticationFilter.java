package org.dwsproject.proyectodesarrolloweb.Controllers;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final UserSession userSession;

    public AuthenticationFilter(UserService userService, UserSession userSession) {
        this.userService = userService;
        this.userSession = userSession;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            String token = authHeader;
            User user = userService.findUserByToken(token);

            if (user != null && user.getToken().equals(token)) {
                //If the token is valid, the user is authenticated and the request is allowed to continue
                userSession.setUser(user);
                request.setAttribute("authenticatedUser", user);
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
