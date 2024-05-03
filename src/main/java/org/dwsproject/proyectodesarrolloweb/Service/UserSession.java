package org.dwsproject.proyectodesarrolloweb.Service;

import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UnauthorizedAccessException;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class UserSession {//information about the actual user

    private final UserRepository userRepository;

    private final UserService userService;
    private User user;
    private int numPosts;

    public UserSession(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        } else {
            return userRepository.findByUsername(authentication.getName());
        }
    }

    public int getNumPosts() {//number of posts that the user has made
        return this.numPosts;
    }

    public void incNumPosts() {//when the user makes a post, the number of posts is increased
        this.numPosts++;
    }

    public void validateUser(String username) throws UnauthorizedAccessException { //check if the user is the same as the logged in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedUsername = authentication.getName();
        User user = userService.findUserByUsername(loggedUsername);
        if (!loggedUsername.equals(username) && !userService.isAdmin(user)) {
            throw new UnauthorizedAccessException("You are not authorized to access this page");
        }
    }
}
