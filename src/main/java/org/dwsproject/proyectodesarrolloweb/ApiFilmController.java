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
@RequestMapping("/api/")//This is the base path for all the endpoints in this class
public class ApiFilmController {

    @Autowired//uses the methods of the service FilmService
    private FilmService filmService;

    @GetMapping("/films")//Get all the films
    public ResponseEntity<List<Pelicula>> getAllFilms() {
        List<Pelicula> films = new ArrayList<>();
        films.addAll(filmService.getPendingFilms());
        films.addAll(filmService.getCompletedFilms());
        return ResponseEntity.ok(films);
    }

    @GetMapping("/films/pending")//Get the pending films
    public ResponseEntity<List<Pelicula>> getPendingFilms() {
        List<Pelicula> films = filmService.getPendingFilms();
        return ResponseEntity.ok(films);
    }

    @GetMapping("/films/completed")//Get the completed films
    public ResponseEntity<List<Pelicula>> getCompletedFilms() {
        List<Pelicula> films = filmService.getCompletedFilms();
        return ResponseEntity.ok(films);
    }

    @DeleteMapping("/films/completed/{title}/")//Delete a film from the completed list by title
    public ResponseEntity<String> deleteFilmC (@PathVariable String title) {
        try {
            filmService.deleteFilm(title, "completed");
            return new ResponseEntity<>("Film deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/films/pending/{title}/")//Delete a film from the pending list by title
    public ResponseEntity<String> deleteFilmP (@PathVariable String title) {
        try {
            filmService.deleteFilm(title, "pending");
            return new ResponseEntity<>("Film deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/addpeli")//Add a film to the list with a JSON and an image
        public ResponseEntity<Pelicula> createFilm(@RequestPart("film") String filmJson, @RequestParam("image") MultipartFile imageFile, @RequestParam("listType") String listType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();//Create an object mapper to convert the JSON to a Pelicula object
            Pelicula film = objectMapper.readValue(filmJson, Pelicula.class);
            filmService.addFilm(film, imageFile, listType);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);//If there is an error, return an internal server error
        }
        if ("pending".equals(listType) || "completed".equals(listType)) {//If the listType is not valid, return a bad request
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
