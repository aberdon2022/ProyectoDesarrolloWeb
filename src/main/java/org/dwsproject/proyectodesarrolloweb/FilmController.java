package org.dwsproject.proyectodesarrolloweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.dwsproject.proyectodesarrolloweb.service.FilmService;

@Controller
public class FilmController {
    @Autowired
    private FilmService filmService;

    @PostMapping("/addpeli")
    public String createFilm(Pelicula film, @RequestParam("image")MultipartFile imageFile, @RequestParam("listType") String listType) {
        try {
            filmService.addFilm(film, imageFile, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Redirect to the corresponding list
        if ("pending".equals(listType)) {
            return "redirect:/ConfirmPending";
        } else if ("completed".equals(listType)) {
            return "redirect:/ConfirmCompleted";
        } else {
            // If listType is not specified, redirect to home page or some other page as needed
            return "redirect:/";
        }
    }

    @GetMapping("/AddPending")
    public String pending(Model model) {
        model.addAttribute("films", filmService.getPendingFilms());
        return "AddPendingList";
    }

    @GetMapping("/AddCompleted")
    public String completed(Model model) {
        model.addAttribute("films", filmService.getCompletedFilms());
        return "AddCompletedList";
    }

    @GetMapping("/ViewPending")
    public String viewPending(Model model) {
        model.addAttribute("films", filmService.getPendingFilms());
        return "ViewPendingList";
    }

    @GetMapping("/ViewCompleted")
    public String viewCompleted(Model model) {
        model.addAttribute("films", filmService.getCompletedFilms());
        return "ViewCompletedList";
    }

    @GetMapping("/ConfirmPending")
    public String confirmPending() {
        return "MessageAfterAddPending";
    }

    @GetMapping("/ConfirmCompleted")
    public String confirmCompleted() {
        return "MessageAfterAddCompleted";
    }

    @GetMapping("/ViewCompleted/{title}/delete")//Delete a film from the completed list
    public String deleteFilmC(Model model, @PathVariable String title) {
        filmService.deleteFilm(title, "completed");
        return "deletedCompletedFilm";
    }
    @GetMapping("/ViewPending/{title}/delete")//Delete a film from the pending list
    public String deleteFilmP(Model model, @PathVariable String title) {
        filmService.deleteFilm(title, "pending");
        return "deletedPendingFilm";
    }
}

   


