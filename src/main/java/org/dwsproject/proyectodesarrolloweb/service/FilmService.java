package org.dwsproject.proyectodesarrolloweb.service;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.FilmRepository;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    public void addFilm(User user, Film film, MultipartFile imageFile, String listType) throws IOException {
        System.out.println("addFilm method called with film title: " + film.getTitle()); // Log when method is called
        //Retrieve the id of the image
        Image image = imageService.createImage(imageFile);
        image = imageService.saveImage(image);
        film.setImageId(image.getId());
        film.setUser(user);

        List<Film> auxList;
        if ("pending".equals(listType)) {
            film.setStatus(Film.FilmStatus.PENDING);
            auxList = user.getPendingFilms();
        } else {
            film.setStatus(Film.FilmStatus.COMPLETED);
            auxList = user.getCompletedFilms();
        }
        auxList.add(film);
        userRepository.save(user);
        filmRepository.save(film);
    }

    public void deleteFilm(User user, long filmId, String listType) throws IOException {
        List<Film> films = "pending".equals(listType) ? user.getPendingFilms() : user.getCompletedFilms();
        Film filmToDelete = null;

        for (Film film : films) {
            if (film.getFilmId() == filmId) {
                filmToDelete = film;
                break;
            }
        }
        if (filmToDelete != null) {
            if ("pending".equals(listType)) {
                user.getPendingFilms().remove(filmToDelete);
            } else {
                user.getCompletedFilms().remove(filmToDelete);
            }
            userRepository.save(user);
            filmRepository.delete(filmToDelete); // Delete the film from the repository
            imageService.deleteImage(filmToDelete.getImageId());
        } else {
            System.out.println("Film not found in the list");
        }
    }

    public ResponseEntity<List<Film>> getAllFilms(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Film> films = new ArrayList<>();
        films.addAll(user.getPendingFilms());
        films.addAll(user.getCompletedFilms());
        return ResponseEntity.ok(films);
    }

    public ResponseEntity<List<Film>> getPendingFilms(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Film> films = user.getPendingFilms();
        return ResponseEntity.ok(films);
    }

    public ResponseEntity<List<Film>> getCompletedFilms(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Film> films = user.getCompletedFilms();
        return ResponseEntity.ok(films);
    }

    public List<Film> findCompletedFilmsByRating(User user, int minRating, int maxRating) {
        return filmRepository.findCompletedFilmsByRating(user, minRating, maxRating);
    }
}