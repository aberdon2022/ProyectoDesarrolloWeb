package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.dwsproject.proyectodesarrolloweb.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<User> profile(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/friends/{username}/add")
    public ResponseEntity<User> addFriend(@PathVariable String username, @RequestParam String friendUsername) {
        User user = userService.findUserByUsername(username);
        User newFriend = userService.findUserByUsername(friendUsername);

        if (username.equals(friendUsername) || user.getFriends().contains(newFriend) || user == null || newFriend == null) {
            return ResponseEntity.badRequest().build();
        }

        user.addFriend(newFriend);
        newFriend.addFriend(user);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/friends/{username}/delete")
    public ResponseEntity<User> deleteFriend(@PathVariable String username, @RequestParam String friendUsername) {
        User user = userService.findUserByUsername(username);
        User friend = userService.findUserByUsername(friendUsername);

        if (user == null || friend == null) {
            return ResponseEntity.badRequest().build();
        }

        user.deleteFriend(friend);

        return ResponseEntity.ok(user);
    }
}