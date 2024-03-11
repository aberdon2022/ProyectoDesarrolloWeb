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
@RequestMapping("/api/")//This is the base path for all the endpoints in this class
public class ApiFilmController {

    @Autowired//uses the methods of the service FilmService
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @GetMapping("/films")//Get all the films
    public ResponseEntity<List<Film>> getAllFilms(@RequestParam String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Film> films = new ArrayList<>();
        films.addAll(user.getPendingFilms());
        films.addAll(user.getCompletedFilms());
        return ResponseEntity.ok(films);
    }

    @GetMapping("/films/pending")//Get the pending films
    public ResponseEntity<List<Film>> getPendingFilms(@RequestParam String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Film> films = user.getPendingFilms();
        return ResponseEntity.ok(films);
    }

    @GetMapping("/films/completed")//Get the completed films
    public ResponseEntity<List<Film>> getCompletedFilms(@RequestParam String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Film> films = user.getCompletedFilms();
        return ResponseEntity.ok(films);
    }

    @PostMapping("/addpeli")//Add a film to the list of films
    public ResponseEntity<Film> createFilm(@RequestPart("film") String filmJson, @RequestParam("image") MultipartFile imageFile, @RequestParam("listType") String listType, @RequestParam("username") String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Film film = objectMapper.readValue(filmJson, Film.class); //Convert the JSON string to a Film object
            filmService.addFilm(user, film, imageFile, listType);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if ("pending".equals(listType) || "completed".equals(listType)) {//If the film is added to the pending or completed list, return a 201 status code
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//If the listType is not pending or completed, return a 400 status code
        }
    }

    @DeleteMapping("/films/completed")
    public ResponseEntity<String> deleteFilmC (@RequestParam String title, @RequestParam String username) {//Delete a film from the completed list
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List <Film> films = user.getCompletedFilms();
        Film f = null;
        for (Film film : films) {
            if (film.getTitle().equals(title)) {
                f = film;
                break;
            }
        }

        if (f == null) {
            return new ResponseEntity<>("Film not found", HttpStatus.NOT_FOUND);
        }

        try {
            filmService.deleteFilm(user, title, "completed");
            return new ResponseEntity<>("Film deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/films/pending")//Delete a film from the pending list
    public ResponseEntity<String> deleteFilmP (@RequestParam String title, @RequestParam String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List <Film> films = user.getPendingFilms();
        Film f = null;
        for (Film film : films) {
            if (film.getTitle().equals(title)) {
                f = film;
                break;
            }
        }

        if (f == null) {
            return new ResponseEntity<>("Film not found", HttpStatus.NOT_FOUND);
        }

        try {
            filmService.deleteFilm(user, title, "pending");
            return new ResponseEntity<>("Film deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
