package org.dwsproject.proyectodesarrolloweb.Controllers;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.dwsproject.proyectodesarrolloweb.Service.FilmService;
import java.io.IOException;
import java.util.List;

@Controller
public class FilmController {

    @Autowired
    private FilmService filmService;//use methods of the service FilmService

    @Autowired
    private UserService userService;//use methods of the service UserService

    @Autowired
    private UserSession userSession;//use methods of the service UserSession

    @PostMapping("/addpeli")//Add a film to the list
    public String createFilm(Film film, @RequestParam("image")MultipartFile imageFile, @RequestParam("listType") String listType, @RequestParam String username) {
        User user = userService.findUserByUsername(username);

        //Check for file type
        String contentType = imageFile.getContentType();
        if (contentType != null) {
            switch (contentType) {
                case "image/jpeg":
                case "image/png":
                case "image/gif":
                case "image/bmp":
                    break;
                default:
                    return "redirect:/error/400";
            }
        }

        try {
            filmService.addFilm(user, film, imageFile, listType);
        } catch (IllegalArgumentException e) {
            return "redirect:/error/400";
        } catch (IOException e) {
            return "redirect:/error/500";
        }
        // Redirect to the corresponding list
        if ("pending".equals(listType)) {
            return "redirect:/pending/confirmed?username=" + username;
        } else if ("completed".equals(listType)) {
            return "redirect:/completed/confirmed?username=" + username;
        } else {
            // If listType is not specified, redirect to home page or some other page as needed
            return "redirect:/";
        }
    }

    @GetMapping("/pending/add")//Show the form to add a film to the pending list
    public String pending(Model model, @RequestParam String username) {
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("films", userService.getPendingFilms(user.getId()));
        return "AddPendingList";
    }

    @GetMapping("/completed/add")//Show the form to add a film to the completed list
    public String completed(Model model, @RequestParam String username) {
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("films", userService.getCompletedFilms(user.getId()));
        return "AddCompletedList";
    }

    @GetMapping("/pending")//Show the pending list
    public String viewPending(Model model, @RequestParam String username, @RequestParam (required = false) String sort, @RequestParam (required = false) String order, @RequestParam (required = false) String title) {
        User loggedInUser = userSession.getUser();

        if (loggedInUser == null || !loggedInUser.getUsername().equals(username)) {
            return "redirect:/error/403";
        }

        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);

        List<Film> pendingFilms = userService.getPendingFilms(user.getId());

        if (sort != null && order != null) {
            pendingFilms = filmService.sortFilms(user, null, null, sort, order, Film.FilmStatus.PENDING);
        }

        if(title != null && !title.isEmpty()){
            pendingFilms = filmService.findPendingFilmsByTitle(user, title);
            if (pendingFilms.isEmpty()) {
                pendingFilms = userService.getPendingFilms(user.getId());
                model.addAttribute("filmNotFound", true);
                model.addAttribute("pending", pendingFilms);
                return "ViewPendingList";
            }
        }

        model.addAttribute("pending", pendingFilms);
        return "ViewPendingList";
    }

    @GetMapping("/completed")//show the completed list
    public String viewCompleted(Model model, @RequestParam String username, @RequestParam (required = false) Integer minRating, @RequestParam (required = false) Integer maxRating, @RequestParam (required = false) String sort, @RequestParam (required = false) String order, @RequestParam (required = false, defaultValue = "false") Boolean applySort, @RequestParam (required = false) String title) {
        User loggedInUser = userSession.getUser();

        if (loggedInUser == null || !loggedInUser.getUsername().equals(username)) {
            return "redirect:/error/403";
        }

        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);

        List<Film> completedFilms;
        if (minRating != null && maxRating != null) { //If minRating and maxRating are specified, filter the films by rating
            completedFilms = filmService.findCompletedFilmsByRating (user,minRating, maxRating);
        } else if (minRating != null) { // MinRating is not null, maxRating is null
            completedFilms = filmService.findCompletedFilmsByRating (user, minRating, 5);
        } else if (maxRating != null) { // MaxRating is not null, minRating is null
            completedFilms = filmService.findCompletedFilmsByRating (user, 0, maxRating);
        } else {
            completedFilms = userService.getCompletedFilms(user.getId());
        }

        if (applySort && sort != null && order != null) {
            completedFilms = filmService.sortFilms(user, minRating, maxRating, sort, order, Film.FilmStatus.COMPLETED);
        }
        if(title != null && !title.isEmpty()){
            completedFilms = filmService.findCompletedFilmsByTitle(user, title);
            if (completedFilms.isEmpty()) {
                completedFilms = userService.getCompletedFilms(user.getId());
                model.addAttribute("filmNotFound", true);
                model.addAttribute("completed", completedFilms);
                return "ViewCompletedList";
            }
        }

        model.addAttribute("completed", completedFilms);
        return "ViewCompletedList";
    }

    @GetMapping("/pending/confirmed")//Show a message after adding a film to the pending list
    public String confirmPending(Model model, @RequestParam String username) {
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "MessageAfterAddPending";
    }

    @GetMapping("/completed/confirmed")//Show a message after adding a film to the completed list
    public String confirmCompleted(Model model, @RequestParam String username) {
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "MessageAfterAddCompleted";
    }

    @GetMapping ("/completed/{filmId}/delete")//Delete a film from the completed list
    public String deleteFilmC(Model model, @PathVariable long filmId, @RequestParam String username) throws IOException {
        User loggedInUser = userSession.getUser();
        if (loggedInUser == null || !loggedInUser.getUsername().equals(username)) {
            return "redirect:/error/403";
        }
        User user = userService.findUserByUsername(username);
        filmService.deleteFilm(user, filmId, "completed");
        model.addAttribute("user", user);
        return "deletedCompletedFilm";
    }
    @GetMapping("/pending/{filmId}/delete")//Delete a film from the pending list
    public String deleteFilmP(Model model, @PathVariable long filmId, @RequestParam String username) throws IOException {
        User loggedInUser = userSession.getUser();

        if (loggedInUser == null || !loggedInUser.getUsername().equals(username)) {
            return "redirect:/error/403";
        }
        User user = userService.findUserByUsername(username);
        filmService.deleteFilm(user, filmId, "pending");
        model.addAttribute("user", user);
        return "deletedPendingFilm";
    }
}

   


