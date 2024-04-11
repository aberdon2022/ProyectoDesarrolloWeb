package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Service.FilmService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class FilmController {

    private final FilmService filmService;//use methods of the service FilmService

    private final UserService userService;//use methods of the service UserService

    private final UserSession userSession;//use methods of the service UserSession

    public FilmController(FilmService filmService, UserService userService, UserSession userSession) {
        this.filmService = filmService;
        this.userService = userService;
        this.userSession = userSession;
    }

    @PostMapping("/addpeli/pending")//Add a film to the list
    public String createPendingFilm(Film film, @RequestParam("image")MultipartFile imageFile, @RequestParam String username) {
        User user = userService.findUserByUsername(username);
        userSession.validateUser(username); //Validate if the user is the same as the one logged in

        System.out.println("Film object in controller: " + film.toString());

        try {
            filmService.addFilmPending(user, film, imageFile);
            return "redirect:/pending/confirmed?username=" + user.getUsername();
        } catch (IllegalArgumentException e) {
            return "redirect:/error/400";
        } catch (IOException e) {
            return "redirect:/error/500";
        }
    }

    @PostMapping("/addpeli/completed")//Add a film to the list
    public String createCompletedFilm(Film film, @RequestParam("image")MultipartFile imageFile, @RequestParam String username) {
        User user = userService.findUserByUsername(username);
        userSession.validateUser(username); //Validate if the user is the same as the one logged in

        System.out.println("Film object in controller: " + film.toString());
        try {
            filmService.addFilmCompleted(user, film, imageFile);
            return "redirect:/completed/confirmed?username=" + user.getUsername();
        } catch (IllegalArgumentException e) {
            return "redirect:/error/400";
        } catch (IOException e) {
            return "redirect:/error/500";
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
        userSession.validateUser(username); //Validate if the user is the same as the one logged in
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
        userSession.validateUser(username);

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

        if (applySort && sort != null && order != null) { // If applySort is true, sort the films by the specified criteria
            completedFilms = filmService.sortFilms(user, minRating, maxRating, sort, order, Film.FilmStatus.COMPLETED);
        }
        if(title != null && !title.isEmpty()){ //If title is not null, filter the films by title
            completedFilms = filmService.findCompletedFilmsByTitle(user, title);
            if (completedFilms.isEmpty()) {
                completedFilms = userService.getCompletedFilms(user.getId());
                model.addAttribute("filmNotFound", true);
                model.addAttribute("completed", completedFilms);
                for (Film film : completedFilms) {
                    film.setRatingStars(filmService.convertRatingToStars(film.getRating()));
                }
                return "ViewCompletedList";
            }
        }

        for (Film film : completedFilms) {
            film.setRatingStars(filmService.convertRatingToStars(film.getRating())); //Convert the rating to stars to show it in the view
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
    public String deleteFilmC(Model model, @PathVariable long filmId, @RequestParam String username) {
        userSession.validateUser(username); //Validate if the user is the same as the one logged in
        User user = userService.findUserByUsername(username);
        filmService.deleteFilm(user, filmId, "completed");
        model.addAttribute("user", user);
        return "deletedCompletedFilm";
    }
    @GetMapping("/pending/{filmId}/delete")//Delete a film from the pending list
    public String deleteFilmP(Model model, @PathVariable long filmId, @RequestParam String username) {
        userSession.validateUser(username); //Validate if the user is the same as the one logged in
        User user = userService.findUserByUsername(username);
        filmService.deleteFilm(user, filmId, "pending");
        model.addAttribute("user", user);
        return "deletedPendingFilm";
    }
}

   


