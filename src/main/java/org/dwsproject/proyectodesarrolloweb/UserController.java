package org.dwsproject.proyectodesarrolloweb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class UserController {
    private ArrayList<User> usersdb = new ArrayList<>();

public ArrayList<User> getUsersdb() {
        return usersdb;
    }

    public void createFakeUsers() {
        usersdb.add(new User("user1", "1"));
    }

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
        createFakeUsers();
        for (User user : usersdb) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                profile(user, model);
                return "redirect:/profile";
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/profile")
    public String profile(User user, Model model) {
        model.addAttribute("user",user);
        return "profile";
    }
}
