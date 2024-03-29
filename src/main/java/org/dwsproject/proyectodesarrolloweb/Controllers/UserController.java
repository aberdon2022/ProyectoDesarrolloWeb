package org.dwsproject.proyectodesarrolloweb.Controllers;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.dwsproject.proyectodesarrolloweb.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;//uses methods of the UserService class

    @Autowired
    private UserSession userSession;//uses methods of the UserSession class

    @GetMapping("/login")  
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @PostMapping("/login")
    public String login(Model model, @RequestParam String username, @RequestParam String password) {
        User user = userService.findUserByUsername(username); //Obtain the user

        if (user != null && userService.checkPassword(user,password)) { //If the user exists and the password is correct
            model.addAttribute("user", user);
            userSession.setUser(user);
            return "redirect:/profile/" + username;
        } else {
            return "redirect:/login?error=true";//If the user does not exist or the password is incorrect return to the login page with an error message
        }
    }

    @GetMapping("/register")
    public String register () {
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        User user = new User(username, password); //Create a new user
        String message = userService.registerUser(user); //Register the user

        if (message.equals("User registered successfully")) {
            model.addAttribute("user", user);
            userSession.setUser(user);
            return "redirect:/profile/" + username;
        } else {
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/register";
        }
    }

    @GetMapping("/profile/{username}")
    public String profile(Model model, @PathVariable String username) {
        User user = userService.findUserByUsername(username);

        if (user != null) {
            model.addAttribute("user", user);
            return "profile";
        } else {
            return "redirect:/login"; // Redirect if user not found
        }
    }

    @GetMapping("/friends/{username}")
    public String friends(Model model, @PathVariable String username) {
        User user = userService.findUserByUsername(username); // Retrieve the user from the database
        User loggedInUserObj = userSession.getUser(); // Retrieve the logged-in user from the database

        if (user != null) {
            model.addAttribute("friend", user);// Add the user's data to the model
            model.addAttribute("friends", userService.getFriends(user));// Add the user's friends to the model
            model.addAttribute("isOwner", user.equals(loggedInUserObj));// Add a boolean to the model that indicates whether the logged-in user is the owner of the profile
            model.addAttribute("loggedInUser", loggedInUserObj);
            return "Friend";
        } else {
            return "redirect:/error/403"; //
        }
    }

    @PostMapping("/friends/{username}/add")
    public String addFriend(Model model, @PathVariable String username, @RequestParam String friendUsername, @RequestParam String loggedInUser, RedirectAttributes redirectAttributes) {

        String message = userService.addFriend(username, friendUsername);
        redirectAttributes.addFlashAttribute("message", message);
        redirectAttributes.addFlashAttribute("loggedInUser", loggedInUser);

        // Redirect to the user's friend list
        return "redirect:/friends/" + loggedInUser  + "?loggedInUser=" + loggedInUser;
    }


    @GetMapping("/friends/{username}/delete")
    public String deleteFriend(Model model, @PathVariable String username, @RequestParam String friendUsername, @RequestParam String loggedInUser, RedirectAttributes redirectAttributes) {

        String message = userService.deleteFriend(username, friendUsername);

        redirectAttributes.addFlashAttribute("message", message);

        // Redirect to the user's friend list
        return "redirect:/friends/" + username + "?loggedInUser=" + loggedInUser;
    }
}