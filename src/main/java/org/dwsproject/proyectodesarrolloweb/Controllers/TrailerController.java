package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Classes.Trailer;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerDeletionException;
import org.dwsproject.proyectodesarrolloweb.Repositories.TrailerRepository;
import org.dwsproject.proyectodesarrolloweb.Service.TrailerService;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerUploadException;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Controller
@RequestMapping("/trailer")
public class TrailerController {

    private final TrailerRepository trailerRepository;

    private final TrailerService trailerService;

    private final UserSession userSession;

    public TrailerController(TrailerRepository trailerRepository, TrailerService trailerService, UserSession userSession) {
        this.trailerRepository = trailerRepository;
        this.trailerService = trailerService;
        this.userSession = userSession;
    }

    @PostMapping("/upload")
    public String uploadTrailer(@RequestParam("file") MultipartFile file, @RequestParam("title") String title, @RequestParam("description") String description, RedirectAttributes redirectAttributes) {

        User user = userSession.getUser();

        try {
            boolean uploadResult = trailerService.uploadTrailer(file, title, description, user);

            if(uploadResult) {
                redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
            } else {
                throw new RuntimeException("Unknown error occurred whilst storing file " + file.getOriginalFilename());
            }
        } catch (TrailerUploadException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
        return "redirect:/trailer/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteTrailer(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userSession.getUser();

        if (user == null || !user.getUsername().equals("admin")) {
            return "redirect:/login";
        }

        model.addAttribute("isAdmin", true);

        try {
            trailerService.deleteTrailer("upload", user, id);

        } catch (IOException | TrailerDeletionException e) {
            redirectAttributes.addFlashAttribute("message", "Error deleting the trailer: " + e.getMessage());
            return "redirect:/trailer/index";
        }
        return "redirect:/trailer/index";
    }

    @GetMapping("/index")
    public String showTrailers(Model model) {
        List<Trailer> trailers = trailerRepository.findAll();
        User user = userSession.getUser();
        if (userSession.getUser() != null && userSession.getUser().getUsername().equals("admin")) {
            model.addAttribute("isAdmin", true);
        }
        model.addAttribute("user", user);
        model.addAttribute("trailers", trailers);
        return "Trailers";
    }

    @GetMapping("/uploads/{id}")
    public ResponseEntity<Object> serveTrailer(@PathVariable Long id) throws MalformedURLException {
        return trailerService.createResponseFromTrailer("uploads", id);
    }
}