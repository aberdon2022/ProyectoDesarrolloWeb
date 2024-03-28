package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Classes.Trailer;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.TrailerRepository;
import org.dwsproject.proyectodesarrolloweb.service.TrailerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.dwsproject.proyectodesarrolloweb.service.UserSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

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

        if (user == null || !user.getUsername().equals("admin")) {
            return "redirect:/login";
        }

        if (title.isEmpty() || description.isEmpty() || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message","The title, description, or file is empty");
            return "redirect:/trailer/index";
        }

        //Check if the file is a video
        if (!Objects.equals(file.getContentType(), "video/mp4")) {
            redirectAttributes.addFlashAttribute("message","The file is not a video");
            return "redirect:/trailer/index";
        }

        try {
            // Store the Metadata in DB
            Trailer trailer = new Trailer();
            trailer.setOriginalFileName(file.getOriginalFilename());
            trailer.setTitle(title);
            trailer.setDescription(description);
            Trailer savedTrailer = trailerRepository.save(trailer); // assuming the save method returns the saved entity

            // Set the correct file path
            String filePath = "src/main/resources/static/uploads/" + "trailer-" + savedTrailer.getId() + ".mp4"; // Ensure to use proper extension
            savedTrailer.setFilePath(filePath);
            trailerRepository.save(savedTrailer);

            // Store the File in System
            trailerService.saveTrailer("uploads", savedTrailer.getId(), file);

            redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
            return "redirect:/trailer/index";
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTrailer(Model model, @PathVariable Long id) throws IOException {
        User user = userSession.getUser();

        if (user == null || !user.getUsername().equals("admin")) {
            return "redirect:/login";
        }

        model.addAttribute("isAdmin", true);

        trailerService.deleteTrailer("uploads", id);
        trailerRepository.deleteById(id);
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