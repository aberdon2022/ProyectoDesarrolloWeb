package org.dwsproject.proyectodesarrolloweb.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.FilmRepository;
import org.dwsproject.proyectodesarrolloweb.Specification.FilmSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FilmService {

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    private final ImageService imageService;

    private final FilmRepository filmRepository;

    private final UserService userService;

    public FilmService(ImageService imageService, FilmRepository filmRepository, UserService userService) {
        this.imageService = imageService;
        this.filmRepository = filmRepository;
        this.userService = userService;
    }

    public void addFilm(User user, Film film, MultipartFile imageFile, String listType) throws IOException {
        System.out.println("addFilm method called with film title: " + film.getTitle()); // Log when method is called
        //Retrieve the id of the image
        Image image = imageService.createImage(imageFile);
        image = imageService.saveImage(image);
        film.setImageId(image.getId());
        film.setUser(user);

        if (!validateFilm(film)) {
            throw new IllegalArgumentException("The film does not exist or could be not validated");
        }

        if ("PENDING".equals(listType)) {
            film.setStatus(Film.FilmStatus.PENDING);
            userService.getPendingFilms(user.getId());

        } else if ("COMPLETED".equals(listType)) {
            film.setStatus(Film.FilmStatus.COMPLETED);
            userService.getCompletedFilms(user.getId());

        } else {
            throw new IllegalArgumentException("Invalid list type");
        }
        filmRepository.save(film);
    }

    public void addFilmPending(User user, Film film, MultipartFile imageFile) throws IOException {
        addFilmWithChecks(user, film, imageFile, Film.FilmStatus.PENDING);
    }

    public void addFilmCompleted(User user, Film film, MultipartFile imageFile) throws IOException {
        addFilmWithChecks(user, film, imageFile, Film.FilmStatus.COMPLETED);
    }

    public boolean deleteFilm(User user, long filmId, String listType) {
        List<Film> films = "pending".equals(listType) ? userService.getPendingFilms(user.getId()) : userService.getCompletedFilms(user.getId());
        Film filmToDelete = null;

        for (Film film : films) {
            if (film.getFilmId() == filmId) {
                filmToDelete = film;
                break;
            }
        }
        if (filmToDelete != null) {
            if ("pending".equals(listType)) {
                userService.getPendingFilms(user.getId()).remove(filmToDelete);
            } else {
                userService.getCompletedFilms(user.getId()).remove(filmToDelete);
            }
            filmRepository.delete(filmToDelete); // Delete the film from the repository
            imageService.deleteImage(filmToDelete.getImageId());
            return true;
        } else {
            System.out.println("Film not found in the list");
            return false;
        }
    }

    public boolean validateFilm (Film film) {
        RestTemplate restTemplate = new RestTemplate();
        String omdbUrl = "https://www.omdbapi.com/?apikey=" + omdbApiKey + "&t=" + film.getTitle() + "&y=" + film.getYear();

        ResponseEntity<String> response;

        try {
            response = restTemplate.getForEntity(omdbUrl, String.class);
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            return false;
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper(); //Parse the JSON response
            try {
                JsonNode root = mapper.readTree(response.getBody()); //Read the JSON response
                if (root.path("Response").asText().equals("True")) { //Check if the response is valid
                    String title = root.path("Title").asText(); //Get the title from the JSON response
                    String plot = root.path("Plot").asText(); //Get the plot from the JSON response
                    film.setPlot(plot); //Set the plot obtained from the JSON response to the film object
                    int year = root.path("Year").asInt(); //Get the year from the JSON response
                    return title.equalsIgnoreCase(film.getTitle()) && year == film.getYear(); //Check if the title and year match the film object
                }
            } catch (JsonProcessingException e ) { //Catch the exception if the JSON response cannot be parsed
                e.printStackTrace();
                return false;
            }
        }
        return false; //Return false if the response is not valid
    }

    public ResponseEntity<List<Film>> getAllFilms(String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Film> films = new ArrayList<>();
        films.addAll(userService.getPendingFilms(user.getId()));
        films.addAll(userService.getCompletedFilms(user.getId()));
        return ResponseEntity.ok(films);
    }

    public ResponseEntity<List<Film>> getPendingFilms(String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Film> films = userService.getPendingFilms(user.getId());
        return ResponseEntity.ok(films);
    }

    public ResponseEntity<List<Film>> getCompletedFilms(String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Film> films = userService.getCompletedFilms(user.getId());
        return ResponseEntity.ok(films);
    }

    public void addFilmWithChecks (User user, Film film, MultipartFile imageFile, Film.FilmStatus status) throws IOException {
        if (user == null || film == null || imageFile == null || status == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        User existingUser = userService.findUserByUsername(user.getUsername());
        if (existingUser == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        System.out.println("Film object before validation: " + film);

        if (film.getTitle() == null || film.getTitle().isEmpty() || film.getYear() < 1900){
            throw new IllegalArgumentException("Invalid film title or year");
        }

        if (isDuplicated(user, film)) {
            throw new IllegalArgumentException("Film already exists");
        }
        addFilm(user, film, imageFile, status.name());
    }

    public boolean isDuplicated (User user, Film film) {
        List<Film> existingFilms = new ArrayList<>();
        existingFilms.addAll(userService.getPendingFilms(user.getId()));
        existingFilms.addAll(userService.getCompletedFilms(user.getId()));

        for (Film f: existingFilms) {
            if (f.getTitle().equalsIgnoreCase(film.getTitle()) && f.getYear() == film.getYear()) {
                return true;
            }
        }
        return false;
    }

    public List<Film> findCompletedFilmsByRating(User user, int minRating, int maxRating) {
        return filmRepository.findAll(Specification.where(FilmSpecification.isCompleted())
                .and(FilmSpecification.hasRatingBetween(minRating, maxRating))
                .and(FilmSpecification.isOwnedByUser(user)));
    }

    public List<Film> findCompletedFilmsByYear(User user, int minRating, int maxRating) {
        return filmRepository.findAll(Specification.where(FilmSpecification.isCompleted())
                .and(FilmSpecification.hasYearBetween(minRating, maxRating))
                .and(FilmSpecification.isOwnedByUser(user)));
    }
    public List<Film> findCompletedFilmsByRatingAndYear(User user, int minRating, int maxRating, int minYear, int maxYear) {
        return filmRepository.findAll(Specification.where(FilmSpecification.isCompleted())
                .and(FilmSpecification.hasYearBetween(minYear, maxYear))
                .and(FilmSpecification.hasRatingBetween(minRating, maxRating))
                .and(FilmSpecification.isOwnedByUser(user)));
    }

    public List<Film> findCompletedFilmsByTitle(User user, String title){
        return filmRepository.findAll(Specification.where(FilmSpecification.isCompleted())
                .and(FilmSpecification.hasThisTitle(title))
                .and(FilmSpecification.isOwnedByUser(user)));
    }

    public List<Film> findPendingFilmsByTitle(User user, String title){
        return filmRepository.findAll(Specification.where(FilmSpecification.isPending())
                .and(FilmSpecification.hasThisTitle(title))
                .and(FilmSpecification.isOwnedByUser(user)));
    }

    public List<Film> sortFilms (User user, Integer minRating, Integer maxRating, Integer minYear, Integer maxYear, String sort, String order, Film.FilmStatus status) {
        Sort sortOrder = Sort.by(sort);

        if ("desc".equals(order)) {
            sortOrder = sortOrder.descending();
        } else {
            sortOrder = sortOrder.ascending();
        }

        Specification<Film> spec = Specification.where(FilmSpecification.hasStatus(status))
                .and(FilmSpecification.isOwnedByUser(user));

        if (minRating != null && maxRating != null) {
            spec = spec.and(FilmSpecification.hasRatingBetween(minRating, maxRating));
        }

        if (minYear != null && maxYear != null) {
            spec = spec.and(FilmSpecification.hasYearBetween(minYear, maxYear));
        }

        return filmRepository.findAll(spec, sortOrder);
    }

    public List<String> convertRatingToStars(int rating) {
        return Collections.nCopies(rating, "★"); //Return a list of stars with the length of the rating
    }
}