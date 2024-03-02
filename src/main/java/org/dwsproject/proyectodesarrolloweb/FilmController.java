package org.dwsproject.proyectodesarrolloweb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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

    // Method yo obtain completed films
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

            film.setImagePath(imageFile.getOriginalFilename());
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

    @DeleteMapping("/delete")
    public String deleteFilm(Pelicula film, @RequestParam("listType") String listType) {
        try {
            if ("pending".equals(listType)) {
                pendingFilms.remove(film);
                return "redirect:/pending";
            } else if ("completed".equals(listType)) {
                completedFilms.remove(film);
                return "redirect:/completed";
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        if ("pending".equals(listType)) {
            return "redirect:/pending";
        } else if ("completed".equals(listType)) {
            return "redirect:/completed";
        } else {
            return "redirect:/";
        }
    }

}    
    


   


