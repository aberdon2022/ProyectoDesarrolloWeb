package org.dwsproject.proyectodesarrolloweb.Controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.dwsproject.proyectodesarrolloweb.Classes.Friendship;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UnauthorizedAccessException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UserAlreadyExistsException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.UserNotFoundException;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.Security.jwt.UserLoginService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.ImageService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final ImageService imageService;

    public ApiUserController(UserService userService, UserSession userSession, UserRepository userRepository, PasswordEncoder passwordEncoder, UserLoginService userLoginService, ImageService imageService) {
        this.userService = userService;
        this.userSession = userSession;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userLoginService = userLoginService;
        this.imageService = imageService;
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

    @PostMapping("/editProfile/{username}")
    public ResponseEntity<User> editProfile(@RequestParam String bio, @RequestParam("profilePicture") MultipartFile profilePicture, @PathVariable String username) throws UnauthorizedAccessException, IOException {
        User authenticatedUser = userSession.getUser();
        if (authenticatedUser == null || !authenticatedUser.getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Access denied");
        }

        User user = userService.findUserByUsername(username);
        if(user != null) {
            user.setBio(bio);
            if (!profilePicture.isEmpty()) {
                Image image = imageService.createImage(profilePicture);
                image = imageService.saveImage(image);
                user.setProfilePicture(image.getId());
            }
            userService.saveUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/editProfile/{username}/delete")
    public ResponseEntity<Void> deleteUserAccount(@PathVariable String username) throws UnauthorizedAccessException {
        User authenticatedUser = userSession.getUser();
        if (authenticatedUser == null || !authenticatedUser.getUsername().equals(username)) {
            throw new UnauthorizedAccessException("Access denied");
        }

        User user = userService.findUserByUsername(username);
        if (user != null) {
            userService.deleteUser(username);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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