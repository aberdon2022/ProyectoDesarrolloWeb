package org.dwsproject.proyectodesarrolloweb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Controller
public class FilmController {
    // List of films
    private ArrayList<Pelicula> films = new ArrayList<>();

    public ArrayList<Pelicula> getAllFilms() {
        return films;
    }
    @PostMapping("/addpeli")
    public String createFilm(Pelicula film, @RequestParam("image")MultipartFile imageFile) {
        try {
            String folder = "src/main/resources/static/images/";
            byte [] bytes = imageFile.getBytes();
            Path path = Paths.get(folder + imageFile.getOriginalFilename());
            Files.write(path, bytes);

            film.setImagePath(imageFile.getOriginalFilename());
            films.add(film);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/pending";
    }

    @GetMapping("/pending")
    public String pending(Model model) {
        model.addAttribute("films", films);
        return "PendingList";
    }
}
