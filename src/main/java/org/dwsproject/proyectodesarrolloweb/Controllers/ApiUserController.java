package org.dwsproject.proyectodesarrolloweb.Controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.dwsproject.proyectodesarrolloweb.Classes.Friendship;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UnauthorizedAccessException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UserAlreadyExistsException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UserNotFoundException;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

    private final UserService userService;
    private final UserSession userSession;
    private final UserRepository userRepository;

    public ApiUserController(UserService userService, UserSession userSession, UserRepository userRepository) {
        this.userService = userService;
        this.userSession = userSession;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public User register(@RequestBody User newUser) throws UserAlreadyExistsException {
        User existingUser = userService.findUserByUsername(newUser.getUsername());

        if (existingUser != null) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        newUser.setPassword(newUser.getPassword());
        newUser.setToken(UUID.randomUUID().toString());

        userRepository.save(newUser);

        return newUser;
    }

    @PostMapping("/login")
    public User login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws UnauthorizedAccessException {
        User user = userRepository.findByUsername(username);

        if (user != null && userService.checkPassword(user, password)) {
            userSession.setUser(user);
            String token = user.getToken();
            response.setHeader("Authorization", token);
            return user;
        } else {
            throw new UnauthorizedAccessException("Invalid username or password");
        }
    }

    @GetMapping("/profile/{username}")
    public User profile(@PathVariable String username) throws UserNotFoundException {
        User user = userService.getUserProfile(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

    @GetMapping("/friends/{username}")
    public Map<String, Object> friends(@PathVariable String username, @RequestHeader(value = "Authorization") String token) throws UnauthorizedAccessException, UserNotFoundException {
        User tokenUser = userService.findUserByToken(token);

        if (tokenUser == null || !tokenUser.getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Invalid token");
        }

        User user = userService.findUserByUsername(username);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("friends", user.getFriends().stream()
                .sorted(Comparator.comparing(Friendship::getTimestamp))
                .map(friendship -> friendship.getUser1().equals(user) ? friendship.getUser2().getUsername() : friendship.getUser1().getUsername())
                .collect(Collectors.toList()));
        return response;
    }

    @PostMapping("/friends/{username}/add")
    public Map<String, String> addFriend(@PathVariable String username, @NotNull @RequestParam String friendUsername) {
        String result = userService.addFriend(username, friendUsername);
        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        return response;
    }

    @DeleteMapping("/friends/{username}/delete")
    public void deleteFriend(@PathVariable String username, @NotNull @RequestParam String friendUsername) {
        userService.deleteFriend(username, friendUsername);
    }
}