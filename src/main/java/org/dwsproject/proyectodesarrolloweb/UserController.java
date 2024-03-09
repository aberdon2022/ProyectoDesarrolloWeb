package org.dwsproject.proyectodesarrolloweb;

import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserController {

    @Autowired
    private UserService userService;//Inyect the service to the controller

    @GetMapping("/login")  //Show the login form
    public String login() {
        return "login";
    }

    @GetMapping("/logout")//Show the logout page
    public String logout() {
        return "logout";
    }

    @PostMapping("/login")//operation to login
    public String login(Model model, @RequestParam String username, @RequestParam String password) {
        User user1 = userService.findUserByUsername("user1"); //Obtain the user user1
        if (user1 != null && username.equals("user1") && password.equals(user1.getPassword())) { //If the user exists and the password is correct
            model.addAttribute("user", user1);
            return "redirect:/profile";//redirect to the profile
        } else {
            return "redirect:/login?error=true";//redirect to the login page with an error message
        }
    }

    @GetMapping("/profile")//Show the profile of the user
    public String profile(Model model) {
        User user = userService.findUserByUsername("user1");
        if (user != null) {
            model.addAttribute("user", user);
            return "profile";
        } else {
            return "redirect:/login"; // Redirigir si no es user1
        }
    }

    @GetMapping("/friends")//Show the friends of the user
    public String friends(Model model) {
        User user = userService.findUserByUsername("user1"); // Obtener el usuario user1
        if (user != null) {
            model.addAttribute("friends", userService.getFriends());
            return "Friends";
        } else {
            return "redirect:/login"; // redirect if not user1
        }
    }
    @GetMapping("/friends/{username}/delete")//Delete a user from the friend list
    public String deleteFriend(Model model, @PathVariable String username) {
        userService.deleteFriends(username);
        return "deletedFriend";
    }
}