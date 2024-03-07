package org.dwsproject.proyectodesarrolloweb;

import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

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
        User user1 = userService.findUserByUsername("user1"); //Obtain the user user1
        if (user1 != null && username.equals("user1") && password.equals(user1.getPassword())) { //If the user exists and the password is correct
            model.addAttribute("user", user1);
            return "redirect:/profile";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = userService.findUserByUsername("user1");
        if (user != null) {
            model.addAttribute("user", user);
            return "profile";
        } else {
            return "redirect:/login"; // Redirigir si no es user1
        }
    }

    @GetMapping("/Friends")
    public String friends(Model model) {
        User user = userService.findUserByUsername("user1"); // Obtener el usuario user1
        if (user != null) {
            // Create a list of fake friends
            ArrayList<User> friends = new ArrayList<>();
            friends.add(new User("user2", "password"));
            friends.add(new User("user3", "password"));
            friends.add(new User("user4", "password"));
            
            model.addAttribute("friends", friends);
            return "Friends";
        } else {
            return "redirect:/login"; // redirect if not user1
        }
    }
}