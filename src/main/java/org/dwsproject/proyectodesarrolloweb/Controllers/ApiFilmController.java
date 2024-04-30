package org.dwsproject.proyectodesarrolloweb.Controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Security.jwt.UserLoginService;
import org.dwsproject.proyectodesarrolloweb.Service.FilmService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class ApiFilmController {

    private final FilmService filmService;

    private final UserService userService;

    private final UserLoginService userLoginService;


    public ApiFilmController(FilmService filmService, UserService userService, UserLoginService userLoginService) {
        this.filmService = filmService;
        this.userService = userService;
        this.userLoginService = userLoginService;
    }

    @GetMapping("/films")
    public ResponseEntity<List<Film>> getAllFilms(@RequestParam String username, HttpServletRequest request) {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // if it is then get all films from user
        return filmService.getAllFilms(username);
    }

    @GetMapping("/films/pending")
    public ResponseEntity<List<Film>> getPendingFilms(@RequestParam String username) {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return filmService.getPendingFilms(username);
    }

    @GetMapping("/films/completed")
    public ResponseEntity<List<Film>> getCompletedFilms(@RequestParam String username) {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return filmService.getCompletedFilms(username);
    }

    @PostMapping("/films/addpeli/pending")
    public ResponseEntity<Void> createPendingFilm (@RequestPart("film") String filmJson, @RequestParam("image") MultipartFile imageFile, @RequestParam("username") String username) {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        try {
            filmService.addFilmPending(userService.findUserByUsername(username), new ObjectMapper().readValue(filmJson, Film.class), imageFile);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/films/addpeli/completed")
    public ResponseEntity<Void> createCompletedFilm (@RequestPart("film") String filmJson, @RequestParam("image") MultipartFile imageFile, @RequestParam("username") String username) {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        try {
            filmService.addFilmCompleted(userService.findUserByUsername(username), new ObjectMapper().readValue(filmJson, Film.class), imageFile);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/films/completed")
    public ResponseEntity<Void> deleteFilmC (@RequestParam long filmId, @RequestParam String username) {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        boolean deleted = filmService.deleteFilm(userService.findUserByUsername(username), filmId, "completed");
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/films/pending")
    public ResponseEntity<Void> deleteFilmP (@RequestParam long filmId, @RequestParam String username) {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        boolean deleted = filmService.deleteFilm(userService.findUserByUsername(username), filmId, "pending");
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
