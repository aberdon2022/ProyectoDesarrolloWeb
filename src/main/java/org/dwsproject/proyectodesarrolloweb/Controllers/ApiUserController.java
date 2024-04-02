package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Classes.Friendship;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.FriendNotFoundException;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> register(@RequestBody User newUser) {
        // Make sure the username isn't taken
        User existingUser = userService.findUserByUsername(newUser.getUsername());

        if (existingUser != null) {
            try {
                throw new UserAlreadyExistsException("Username already exists");
            } catch (UserAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        }

        // Set password and token for new user
        newUser.setPassword(newUser.getPassword()); // you can use a password encoder
        newUser.setToken(UUID.randomUUID().toString());

        userRepository.save(newUser);

        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && userService.checkPassword(user, password)) {
            userSession.setUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<User> profile(@PathVariable String username) {
        User user = userService.getUserProfile(username);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/friends/{username}")
    public ResponseEntity<Map<String, Object>> friends(@PathVariable String username, @RequestHeader(value = "Authorization") String token) {
        User tokenUser = userService.findUserByToken(token);

        if (tokenUser == null || !tokenUser.getUsername().equals(username)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserByUsername(username);

        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("friends", user.getFriends().stream()
                    .sorted(Comparator.comparing(Friendship::getTimestamp))
                    .map(friendship -> friendship.getUser1().equals(user) ? friendship.getUser2().getUsername() : friendship.getUser1().getUsername())
                    .collect(Collectors.toList()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/friends/{username}/add")
    public ResponseEntity<Map<String, String>> addFriend(@PathVariable String username, @RequestParam String friendUsername) {
        Map<String, String> response = new HashMap<>();
        if (username == null || friendUsername == null) {
            response.put("message", "Username or friend username not provided");
            return ResponseEntity.badRequest().body(response);
        }

        String result = userService.addFriend(username, friendUsername);
        response.put("message", result);
        if (result.equals("Friend added successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/friends/{username}/delete")
    public ResponseEntity<User> deleteFriend(@PathVariable String username, @RequestParam String friendUsername) {
        if (username == null || friendUsername == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            userService.deleteFriend(username, friendUsername);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (FriendNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}