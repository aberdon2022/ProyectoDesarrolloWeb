package org.dwsproject.proyectodesarrolloweb.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.FilmRepository;
import org.dwsproject.proyectodesarrolloweb.Specification.FilmSpecification;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Service
public class FilmService {

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UserService userService;

    public void saveFilm(Film film) {
        filmRepository.save(film);
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

        if ("pending".equals(listType)) {
            film.setStatus(Film.FilmStatus.PENDING);
            userService.getPendingFilms(user.getId());
        } else {
            film.setStatus(Film.FilmStatus.COMPLETED);
            userService.getCompletedFilms(user.getId());
        }
        filmRepository.save(film);
        userService.saveUser(user);
    }

    public void deleteFilm(User user, long filmId, String listType) throws IOException {
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
            userService.saveUser(user);
            filmRepository.delete(filmToDelete); // Delete the film from the repository
            imageService.deleteImage(filmToDelete.getImageId());
        } else {
            System.out.println("Film not found in the list");
        }
    }

    public boolean validateFilm (Film film) {
        RestTemplate restTemplate = new RestTemplate();
        String omdbUrl = "http://www.omdbapi.com/?apikey=" + omdbApiKey + "&t=" + film.getTitle() + "&y=" + film.getYear();

        ResponseEntity<String> response = null;

        try {
            response = restTemplate.getForEntity(omdbUrl, String.class);
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            return false;
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(response.getBody());
                if (root.path("Response").asText().equals("True")) {
                    String title = root.path("Title").asText();
                    String plot = root.path("Plot").asText();
                    film.setPlot(plot);
                    int year = root.path("Year").asInt();
                    return title.equals(film.getTitle()) && year == film.getYear();
                }
            } catch (JsonProcessingException e ) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
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

    public List<Film> findCompletedFilmsByRating(User user, int minRating, int maxRating) {
        return filmRepository.findAll(Specification.where(FilmSpecification.isCompleted())
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

    public List<Film> sortFilms (User user, Integer minRating, Integer maxRating, String sort, String order, Film.FilmStatus status) {
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

        return filmRepository.findAll(spec, sortOrder);
    }
}