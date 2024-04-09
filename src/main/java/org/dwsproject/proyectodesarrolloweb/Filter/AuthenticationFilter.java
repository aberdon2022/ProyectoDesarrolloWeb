package org.dwsproject.proyectodesarrolloweb.Filter;

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

        if (token != null) { // If token is not null, then the user is authenticated
            User user = userService.findUserByToken(token);
            String username = getUsername(request, user);

            if (isUserAuthenticated(user, token, username)) { // If the user is authenticated, set the user session and continue
                setUserSessionAndContinueFilter(user, request, response, filterChain);
            } else {
                rejectRequest(response); // If the user is not authenticated, reject the request
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
        int lastSlashIndex = requestURI.lastIndexOf('/'); // Get the last slash index
        if (lastSlashIndex >= 0) { // If the last slash index is greater than or equal to 0
            String username = requestURI.substring(lastSlashIndex + 1); // Get the substring from the last slash index + 1
            User potentialUser = userService.findUserByUsername(username); // Find if the user exists
            if (potentialUser != null) { // If the user exists return the username
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
        filterChain.doFilter(request, response);
    }

    private void rejectRequest(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}