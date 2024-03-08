package org.dwsproject.proyectodesarrolloweb;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dwsproject.proyectodesarrolloweb.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class ApiFilmController {

    @Autowired
    private FilmService filmService;

    @GetMapping("/films")
    public ResponseEntity<List<Pelicula>> getAllFilms() {
        List<Pelicula> films = new ArrayList<>();
        films.addAll(filmService.getPendingFilms());
        films.addAll(filmService.getCompletedFilms());
        return ResponseEntity.ok(films);
    }

    @GetMapping("/films/pending")
    public ResponseEntity<List<Pelicula>> getPendingFilms() {
        List<Pelicula> films = filmService.getPendingFilms();
        return ResponseEntity.ok(films);
    }

    @GetMapping("/films/completed")
    public ResponseEntity<List<Pelicula>> getCompletedFilms() {
        List<Pelicula> films = filmService.getCompletedFilms();
        return ResponseEntity.ok(films);
    }

    @DeleteMapping("/films/completed/{title}/")
    public ResponseEntity<String> deleteFilmC (@PathVariable String title) {
        try {
            filmService.deleteFilm(title, "completed");
            return new ResponseEntity<>("Film deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/films/pending/{title}/")
    public ResponseEntity<String> deleteFilmP (@PathVariable String title) {
        try {
            filmService.deleteFilm(title, "pending");
            return new ResponseEntity<>("Film deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/addpeli")
        public ResponseEntity<Pelicula> createFilm(@RequestPart("film") String filmJson, @RequestParam("image") MultipartFile imageFile, @RequestParam("listType") String listType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Pelicula film = objectMapper.readValue(filmJson, Pelicula.class);
            filmService.addFilm(film, imageFile, listType);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if ("pending".equals(listType) || "completed".equals(listType)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
