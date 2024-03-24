package org.dwsproject.proyectodesarrolloweb.Controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.service.FilmService;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
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

    @Autowired
    private UserService userService;

    @GetMapping("/films")
    public ResponseEntity<List<Film>> getAllFilms(@RequestParam String username) {
        return filmService.getAllFilms(username);
    }

    @GetMapping("/films/pending")
    public ResponseEntity<List<Film>> getPendingFilms(@RequestParam String username) {
        return filmService.getPendingFilms(username);
    }

    @GetMapping("/films/completed")
    public ResponseEntity<List<Film>> getCompletedFilms(@RequestParam String username) {
        return filmService.getCompletedFilms(username);
    }

    @PostMapping("/addpeli")
    public ResponseEntity<Void> createFilm (@RequestPart("film") String filmJson, @RequestParam("image") MultipartFile imageFile, @RequestParam("listType") String listType, @RequestParam("username") String username) {
        try {
            filmService.addFilm(userService.findUserByUsername(username), new ObjectMapper().readValue(filmJson, Film.class), imageFile, listType);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/films/completed")
    public ResponseEntity<Void> deleteFilmC (@RequestParam long filmId, @RequestParam String username) {
        try {
            filmService.deleteFilm(userService.findUserByUsername(username), filmId, "completed");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/films/pending")
    public ResponseEntity<Void> deleteFilmP (@RequestParam long filmId, @RequestParam String username) {
        try {
            filmService.deleteFilm(userService.findUserByUsername(username), filmId, "pending");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
