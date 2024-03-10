package org.dwsproject.proyectodesarrolloweb;

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

    @PostMapping("/addpeli")//Add a film to the list
    public String createFilm(Pelicula film, @RequestParam("image")MultipartFile imageFile, @RequestParam("listType") String listType) {
        try {
            filmService.addFilm(film, imageFile, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Redirect to the corresponding list
        if ("pending".equals(listType)) {
            return "redirect:/pending/confirmed";
        } else if ("completed".equals(listType)) {
            return "redirect:/completed/confirmed";
        } else {
            // If listType is not specified, redirect to home page or some other page as needed
            return "redirect:/";
        }
    }

    @GetMapping("/pending/add")//Show the form to add a film to the pending list
    public String pending(Model model) {
        model.addAttribute("films", filmService.getPendingFilms());
        return "AddPendingList";
    }

    @GetMapping("/completed/add")//Show the form to add a film to the completed list
    public String completed(Model model) {
        model.addAttribute("films", filmService.getCompletedFilms());
        return "AddCompletedList";
    }

    @GetMapping("/pending")//Show the pending list
    public String viewPending(Model model) {
        model.addAttribute("films", filmService.getPendingFilms());
        return "ViewPendingList";
    }

    @GetMapping("/completed")//show the completed list
    public String viewCompleted(Model model) {
        model.addAttribute("films", filmService.getCompletedFilms());
        return "ViewCompletedList";
    }

    @GetMapping("/pending/confirmed")//Show a message after adding a film to the pending list
    public String confirmPending() {
        return "MessageAfterAddPending";
    }

    @GetMapping("/completed/confirmed")//Show a message after adding a film to the completed list
    public String confirmCompleted() {
        return "MessageAfterAddCompleted";
    }

    @GetMapping ("/completed/{title}/delete")//Delete a film from the completed list
    public String deleteFilmC(Model model, @PathVariable String title) {
        filmService.deleteFilm(title, "completed");
        return "deletedCompletedFilm";
    }
    @GetMapping("/pending/{title}/delete")//Delete a film from the pending list
    public String deleteFilmP(Model model, @PathVariable String title) {
        filmService.deleteFilm(title, "pending");
        return "deletedPendingFilm";
    }
}

   


