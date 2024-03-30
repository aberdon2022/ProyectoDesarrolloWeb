package org.dwsproject.proyectodesarrolloweb.Controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dwsproject.proyectodesarrolloweb.Classes.Trailer;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerNotFoundException;
import org.dwsproject.proyectodesarrolloweb.Service.TrailerService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class ApiTrailerController {
    @Autowired
    private TrailerService trailerService;

    @Autowired
    private UserService userService;

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
    public ResponseEntity<Void> deleteTrailer(@RequestParam long trailerId, @RequestParam String username) {
        try {
            trailerService.deleteTrailer("upload", userService.findUserByUsername(username), trailerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } 
        catch (TrailerNotFoundException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
    }
      @GetMapping("/uploads/{id}")
    public ResponseEntity<Object> serveTrailer(@PathVariable Long id) throws MalformedURLException {
        return trailerService.createResponseFromTrailer("uploads", id);
    }
    
}
