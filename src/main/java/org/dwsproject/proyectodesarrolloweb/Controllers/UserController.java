package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.dwsproject.proyectodesarrolloweb.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            userSession.setUser(user.getUsername());
            return "redirect:/profile/" + username;
        } else {
            return "redirect:/login?error=true";//If the user does not exist or the password is incorrect return to the login page with an error message
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
    public String friends(Model model, @PathVariable String username, @RequestParam String loggedInUser) {
        User user = userService.findUserByUsername(username); // Retrieve the user based on the username path variable
        if (user != null) {
            model.addAttribute("friend", user);// Add the user's data to the model
            model.addAttribute("friends", user.getFriends());// Add the user's friends to the model
            model.addAttribute("isOwner", loggedInUser.equals(username));// Add a boolean to the model that indicates whether the logged-in user is the owner of the profile
            return "Friend";
        } else {
            return "redirect:/login"; // Redirect if user not found
        }
    }

    @PostMapping("/friends/{username}/add")
    public String addFriend(Model model, @PathVariable String username, @RequestParam String friendUsername, @RequestParam String loggedInUser) {
        // Retrieve the user's data
        User user = userService.findUserByUsername(username);

        // Retrieve the friend's data
        User newFriend = userService.findUserByUsername(friendUsername);

        if (user == null || newFriend == null) {
            // Redirect to an error page or another appropriate page
            return "redirect:/error";
        }

        // Add the new friend to the user's friend list
        user.addFriend(newFriend);

        newFriend.addFriend(user);

        // Debugging code
        System.out.println("Friend added: " + newFriend.getUsername());

        // Update the model
        model.addAttribute("friend", user);
        model.addAttribute("friends", user.getFriends());

        // Redirect to the user's friend list
        return "redirect:/friends/" + loggedInUser  + "?loggedInUser=" + loggedInUser;
    }

    @GetMapping("/friends/{username}/delete")
    public String deleteFriend(Model model, @PathVariable String username, @RequestParam String friendUsername, @RequestParam String loggedInUser) {
        // Retrieve the user's data
        User user = userService.findUserByUsername(username);

        // Retrieve the friend's data
        User friend = userService.findUserByUsername(friendUsername);


        if (!loggedInUser.equals(username)) {
            return "redirect:/error";
        }

        // Debugging code
        if (user == null) {
            System.out.println("User is null. No user found with username: " + username);
        }
        if (friend == null) {
            System.out.println("Friend is null. No friend found with username: " + friendUsername);
        }

        if (user == null || friend == null) {
            // Redirect to an error page or another appropriate page
            return "redirect:/error";
        }

        // Delete the friend from the user's friend list
        user.deleteFriend(friend);

        // Debugging code
        System.out.println("Friend deleted: " + friend.getUsername());

        // Update the model
        model.addAttribute("friend", user);
        model.addAttribute("friends", user.getFriends());

        // Redirect to the user's friend list
        return "redirect:/friends/" + username + "?loggedInUser=" + loggedInUser;
    }
}