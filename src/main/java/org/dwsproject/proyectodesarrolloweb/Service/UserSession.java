package org.dwsproject.proyectodesarrolloweb.Service;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UnauthorizedAccessException;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.hibernate.annotations.AttributeAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Base64;
import java.util.StringTokenizer;
import java.util.UUID;

@Component
@SessionScope
public class UserSession {//information about the actual user

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    private User user;
    private int numPosts;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public int getNumPosts() {//number of posts that the user has made
        return this.numPosts;
    }

    public void incNumPosts() {//when the user makes a post, the number of posts is increased
        this.numPosts++;
    }

    public void validateUser(String username) {
        User user = userRepository.findByUsername(username);
        User loggedInUser = this.getUser();
        if (user == null || !user.equals(loggedInUser)) {
            try {
                throw new UnauthorizedAccessException("Unauthorized access");
            } catch (UnauthorizedAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean validateUserToken(String token) {
        User user = userRepository.findByToken(token);
        return user != null;
    }

    public String loginUser(String username, String password) throws UnauthorizedAccessException {
        User user = userRepository.findByUsername(username);
        if(user != null && userService.checkPassword(user,password)) {
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            userRepository.save(user);
            return token;
        } else {
            throw new UnauthorizedAccessException("Username or password is incorrect.");
        }
    }
}
