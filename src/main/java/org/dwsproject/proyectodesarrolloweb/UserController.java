package org.dwsproject.proyectodesarrolloweb;

import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;//Inyect the service to the controller

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
        if (user != null && username.equals(username) && password.equals(user.getPassword())) { //If the user exists and the password is correct
            model.addAttribute("user", user);
            return "redirect:/profile/" + username;
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/profile/{username}")
    public String profile(Model model, @PathVariable String username) {
        User user = userService.findUserByUsername(username);
        if (user != null) {
            model.addAttribute("user", user);
            return "profile";
        } else {
            return "redirect:/login"; // Redirigir si no es user1
        }
    }

    @GetMapping("/friends/{username}")
    public String friends(Model model, @PathVariable String username, @RequestParam String loggedInUser) {
        User user = userService.findUserByUsername(username); // Retrieve the user based on the username path variable
        if (user != null) {
            model.addAttribute("friend", user);
            model.addAttribute("friends", user.getFriends());
            model.addAttribute("isOwner", loggedInUser.equals(username));
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

        // Debugging code
        System.out.println("Friend added: " + newFriend.getUsername());

        // Update the model
        model.addAttribute("friend", user);
        model.addAttribute("friends", user.getFriends());

        // Redirect to the user's friend list
        return "redirect:/friends/" + loggedInUser  + "?loggedInUser=" + loggedInUser;
    }
 /*   @GetMapping("/friends/{username}/show")
    public String showFriend(Model model, @PathVariable String username) {
        // Retrieve the friend's data, including their list of friends
        User friend = userService.findUserByUsername(username);

        if (friend == null) {
            // Redirect to an error page or another appropriate page
            return "redirect:/error";
        }

        List<User> friends = friend.getFriends();

        // Add the friend's data to the model
        model.addAttribute("friend", friend);
        model.addAttribute("friends", friends);

        // Return the name of the Mustache template that displays the friend's page
        return "Friend";
    }*/

    @GetMapping("/friends/{username}/delete")
    public String deleteFriend(Model model, @PathVariable String username, @RequestParam String friendUsername, @RequestParam String loggedInUser) {
        // Retrieve the user's data
        User user = userService.findUserByUsername(username);

        // Retrieve the friend's data
        User friend = userService.findUserByUsername(friendUsername);

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