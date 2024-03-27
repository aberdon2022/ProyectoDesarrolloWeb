package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Classes.Friendship;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.dwsproject.proyectodesarrolloweb.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && userService.checkPassword(user,password)) {
            userSession.setUser(user);
            return ResponseEntity.ok(user);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<User> profile(@PathVariable String username) {
        User user = userService.findUserByUsername(username);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/friends/{username}")
    public ResponseEntity<Map<String, Object>> friends(@PathVariable String username) {
        User user = userService.findUserByUsername(username);

        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("friends", user.getFriends().stream()
                    .sorted(Comparator.comparing(Friendship::getTimestamp))
                    .map(friendship -> friendship.getUser1().equals(user) ? friendship.getUser2().getUsername() : friendship.getUser1().getUsername())
                    .collect(Collectors.toList()));
            return ResponseEntity.ok(response);
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
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/friends/{username}/delete")
    public ResponseEntity<User> deleteFriend(@PathVariable String username, @RequestParam String friendUsername) {
        if (username == null || friendUsername == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String response = userService.deleteFriend(username, friendUsername);
        if (response.equals("Friend deleted successfully.")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}