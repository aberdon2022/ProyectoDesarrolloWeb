package org.dwsproject.proyectodesarrolloweb.Controllers;
import org.dwsproject.proyectodesarrolloweb.Classes.Trailer;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerNotFoundException;
import org.dwsproject.proyectodesarrolloweb.Service.TrailerService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class ApiTrailerController {

    private final TrailerService trailerService;

    private final UserService userService;

    public ApiTrailerController(TrailerService trailerService, UserService userService) {
        this.trailerService = trailerService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadTrailer(@RequestParam("file") MultipartFile file, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("username") String username) {
        try {
            trailerService.uploadTrailer(file, title, description, userService.findUserByUsername(username));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/index")
    public ResponseEntity<List<Trailer>> getAllTrailers() {
        List<Trailer> trailers = trailerService.getAllTrailers();
        return new ResponseEntity<>(trailers, HttpStatus.OK);
    }

  @DeleteMapping("/index")
    public ResponseEntity<String> deleteTrailer(@RequestParam long trailerId, @RequestParam String username) {
        try {
            trailerService.deleteTrailer("upload", userService.findUserByUsername(username), trailerId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (TrailerNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
  }
  @GetMapping("/uploads/{id}")
    public ResponseEntity<Object> searchById(@PathVariable Long id){
        try {
            Trailer trailer = trailerService.searchById(id);
            return new ResponseEntity<>(trailer,HttpStatus.OK);
        } catch (TrailerNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
