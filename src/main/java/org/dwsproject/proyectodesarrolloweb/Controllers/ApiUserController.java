package org.dwsproject.proyectodesarrolloweb.Controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.dwsproject.proyectodesarrolloweb.Classes.Friendship;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UnauthorizedAccessException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UserAlreadyExistsException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UserNotFoundException;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.Security.jwt.UserLoginService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class ApiUserController {

    private final UserService userService;
    private final UserSession userSession;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserLoginService userLoginService;

    public ApiUserController(UserService userService, UserSession userSession, UserRepository userRepository, PasswordEncoder passwordEncoder, UserLoginService userLoginService) {
        this.userService = userService;
        this.userSession = userSession;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userLoginService = userLoginService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User newUser) throws UserAlreadyExistsException {
        User existingUser = userService.findUserByUsername(newUser.getUsername());

        if (existingUser != null) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        userRepository.save(newUser);

        return newUser;
    }


    @GetMapping("/profile/{username}")
    public ResponseEntity<User> profile(@PathVariable String username) throws UserNotFoundException, UnauthorizedAccessException {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken) && !userService.isAdmin(userSession.getUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        userService.setUserPendingFilms(user);
        userService.setUserCompletedFilms(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/friends/{username}")
    public Map<String, Object> friends(@PathVariable String username) throws UnauthorizedAccessException, UserNotFoundException {
        User authenticatedUser = userSession.getUser();

        if ((authenticatedUser == null || !authenticatedUser.getUsername().equals(username)) && !userService.isAdmin(authenticatedUser)) {
            throw new UnauthorizedAccessException("Access denied");
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
    public Map<String, String> addFriend(@PathVariable String username, @Valid @NotNull @RequestParam String friendUsername) throws UnauthorizedAccessException {
        User authenticatedUser = userSession.getUser();

        if (authenticatedUser == null || !authenticatedUser.getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Access denied");
        }

        String result = userService.addFriend(username, friendUsername);
        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        return response;
    }

    @DeleteMapping("/friends/{username}/delete")
    public void deleteFriend(@PathVariable String username, @Valid @NotNull @RequestParam String friendUsername) throws UnauthorizedAccessException {
        User authenticatedUser = userSession.getUser();

        if (authenticatedUser == null || !authenticatedUser.getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Access denied");
        }

        userService.deleteFriend(username, friendUsername);
    }
}