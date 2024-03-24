package org.dwsproject.proyectodesarrolloweb.service;
import jakarta.transaction.Transactional;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.FilmRepository;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FilmService {

    @Autowired
    private ImageService imageService;
    private AtomicLong nextId = new AtomicLong();//Create an id for the posts

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    public void addFilm(User user, Film film, MultipartFile imageFile, String listType) throws IOException {
        System.out.println("addFilm method called with film title: " + film.getTitle()); // Log when method is called
        //Retrieve the id of the image
        long imageId = imageService.getNextId();
        film.setImageId(imageId);
        imageService.saveImage("FilmsImages", film.getImageId(), imageFile);//Save the image in the folder
        film.setUser(user);

        if ("pending".equals(listType)) {
            film.setStatus(Film.FilmStatus.PENDING);
        } else {
            film.setStatus(Film.FilmStatus.COMPLETED);
        }

        filmRepository.save(film);
        user = userRepository.findById(user.getId()).orElse(null); // Retrieve the user again
        if (user != null) {
            if ("pending".equals(listType)) {
                user.getPendingFilms().add(film);
            } else {
                user.getCompletedFilms().add(film);
            }
            userRepository.save(user);
        }
    }

    @Transactional
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
            filmRepository.delete(filmToDelete); // Delete the film from the repository
            imageService.deleteImage("FilmsImages", filmToDelete.getImageId());
            userRepository.save(user); // Save the user after removing the film from the list
        } else {
            System.out.println("Film not found in the list");
        }
    }
}