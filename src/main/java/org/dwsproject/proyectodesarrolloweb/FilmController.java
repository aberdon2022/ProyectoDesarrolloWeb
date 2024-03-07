package org.dwsproject.proyectodesarrolloweb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FilmController {
    // List of films
    private ArrayList<Pelicula> pendingFilms = new ArrayList<>();
    private ArrayList<Pelicula> completedFilms = new ArrayList<>();

     // Method to obtain pending films
     public List<Pelicula> getPendingFilms() {
        return pendingFilms;
    }

    // Method to obtain completed films
    public List<Pelicula> getCompletedFilms() {
        return completedFilms;
    }
    
    @PostMapping("/addpeli")
    public String createFilm(Pelicula film, @RequestParam("image")MultipartFile imageFile, @RequestParam("listType") String listType) {
        try {
            String folder = "src/main/resources/static/images/";
            byte[] bytes = imageFile.getBytes();
            Path path = Paths.get(folder + imageFile.getOriginalFilename());
            Files.write(path, bytes);

            film.setImagePath(imageFile.getOriginalFilename());//Save the image path
            if ("pending".equals(listType)) {
                pendingFilms.add(film);
            } else if ("completed".equals(listType)) {
                completedFilms.add(film);
            }
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
        model.addAttribute("films", pendingFilms);
        return "AddPendingList";
    }
    
    @GetMapping("/AddCompleted")
        public String completed(Model model) {
            model.addAttribute("films", completedFilms);
            return "AddCompletedList";
    }

    @GetMapping("/ViewPending")
    public String viewPending(Model model) {
        model.addAttribute("films", pendingFilms);
        return "ViewPendingList";
    }

    @GetMapping("/ConfirmPending")
    public String confirmPending() {
        return "MessageAfterAddPending";
    }

    @GetMapping("/ViewCompleted")
    public String viewCompleted(Model model) {
        model.addAttribute("films", completedFilms);
        return "ViewCompletedList";
    }

    @GetMapping("/ConfirmCompleted")
    public String confirmCompleted() {
        return "MessageAfterAddCompleted";
    }

    @GetMapping("/ViewCompleted/{title}/delete")//Delete a film from the completed list
    public String deleteFilmC(Model model, @PathVariable String title) {
        completedFilms.removeIf(p -> p.getTitle().equals(title));
        return "deletedCompletedFilm";
    }
    @GetMapping("/ViewPending/{title}/delete")//Delete a film from the pending list
    public String deleteFilmP(Model model, @PathVariable String title) {
        pendingFilms.removeIf(p -> p.getTitle().equals(title));
        return "deletedPendingFilm";
    }



}    
    


   


