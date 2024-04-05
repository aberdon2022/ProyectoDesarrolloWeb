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
        String token = request.getHeader("Authorization");

        if (token != null) {
            User user = userService.findUserByToken(token);
            String username = getUsername(request, user);

            if (isUserAuthenticated(user, token, username)) {
                setUserSessionAndContinueFilter(user, request, response, filterChain);
            } else {
                rejectRequest(response);
            }
        } else {
            rejectRequest(response);
        }
    }

    private String getUsername(HttpServletRequest request, User user) {
        String username = request.getParameter("username");
        if (username == null) {
            username = getUsernameFromURI(request);
        }
        if (username == null && user != null) {
            username = user.getUsername();
        }
        return username;
    }

    private String getUsernameFromURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        int lastSlashIndex = requestURI.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            String username = requestURI.substring(lastSlashIndex + 1);
            User potentialUser = userService.findUserByUsername(username);
            if (potentialUser != null) {
                return username;
            }
        }
        return null;
    }

    private boolean isUserAuthenticated(User user, String token, String username) {
        return user != null && user.getToken().equals(token) && user.getUsername().equals(username);
    }

    private void setUserSessionAndContinueFilter(User user, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        userSession.setUser(user);
        request.setAttribute("authenticatedUser", user);
        filterChain.doFilter(request, response);
    }

    private void rejectRequest(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}