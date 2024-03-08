package org.dwsproject.proyectodesarrolloweb.service;

import org.dwsproject.proyectodesarrolloweb.Pelicula;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {

    private ArrayList<Pelicula> pendingFilms = new ArrayList<>();
    private ArrayList<Pelicula> completedFilms = new ArrayList<>();

    public List<Pelicula> getPendingFilms() {
        return pendingFilms;
    }

    public List<Pelicula> getCompletedFilms() {
        return completedFilms;
    }

    public void addFilm(Pelicula film, MultipartFile imageFile, String listType) throws IOException {
        String folder = "src/main/resources/static/images/";
        byte[] bytes = imageFile.getBytes();
        Path path = Paths.get(folder + imageFile.getOriginalFilename());
        Files.write(path, bytes);

        film.setImagePath(imageFile.getOriginalFilename());//Save the image path
        if ("pending".equals(listType)) {
            pendingFilms.add(film);
        } else if ("completed".equals(listType)) {
            completedFilms.add(film);
        }
    }

    public void deleteFilm(String title, String listType) {
        if ("pending".equals(listType)) {
            pendingFilms.removeIf(p -> p.getTitle().equals(title));
        } else if ("completed".equals(listType)) {
            completedFilms.removeIf(p -> p.getTitle().equals(title));
        }
    }
}