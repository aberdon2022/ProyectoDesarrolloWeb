package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Classes.Pelicula;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.dwsproject.proyectodesarrolloweb.service.FilmService;

@Controller
public class FilmController {
    @Autowired
    private FilmService filmService;//use methods of the service FilmService

    @Autowired
    private UserService UserService;//use methods of the service UserService

    @PostMapping("/addpeli")//Add a film to the list
    public String createFilm(Pelicula film, @RequestParam("image")MultipartFile imageFile, @RequestParam("listType") String listType, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);

        try {
            filmService.addFilm(user, film, imageFile, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Redirect to the corresponding list
        if ("pending".equals(listType)) {
            return "redirect:/pending/confirmed?username=" + username;
        } else if ("completed".equals(listType)) {
            return "redirect:/completed/confirmed?username=" + username;
        } else {
            // If listType is not specified, redirect to home page or some other page as needed
            return "redirect:/";
        }
    }

    @GetMapping("/pending/add")//Show the form to add a film to the pending list
    public String pending(Model model, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("films", user.getPendingFilms());
        return "AddPendingList";
    }

    @GetMapping("/completed/add")//Show the form to add a film to the completed list
    public String completed(Model model, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("films", user.getCompletedFilms());
        return "AddCompletedList";
    }

    @GetMapping("/pending")//Show the pending list
    public String viewPending(Model model, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("films", user.getPendingFilms());
        return "ViewPendingList";
    }

    @GetMapping("/completed")//show the completed list
    public String viewCompleted(Model model, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("films", user.getCompletedFilms());
        return "ViewCompletedList";
    }

    @GetMapping("/pending/confirmed")//Show a message after adding a film to the pending list
    public String confirmPending(Model model, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "MessageAfterAddPending";
    }

    @GetMapping("/completed/confirmed")//Show a message after adding a film to the completed list
    public String confirmCompleted(Model model, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "MessageAfterAddCompleted";
    }

    @GetMapping ("/completed/{title}/delete")//Delete a film from the completed list
    public String deleteFilmC(Model model, @PathVariable String title, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);
        filmService.deleteFilm(user, title, "completed");
        model.addAttribute("user", user);
        return "deletedCompletedFilm";
    }
    @GetMapping("/pending/{title}/delete")//Delete a film from the pending list
    public String deleteFilmP(Model model, @PathVariable String title, @RequestParam String username) {
        User user = UserService.findUserByUsername(username);

        if (user == null) {
            return "redirect:/";
        }
        
        filmService.deleteFilm(user, title, "pending");
        model.addAttribute("user", user);
        return "deletedPendingFilm";
    }
}

   


