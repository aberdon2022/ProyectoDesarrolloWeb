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
import java.util.Map;
import java.util.HashMap;

@Service
public class FilmService {

    private ArrayList<Pelicula> pendingFilms = new ArrayList<>();//Create a list of pending films
    private ArrayList<Pelicula> completedFilms = new ArrayList<>();//Create a list of completed films
    private Map<String, byte[]> imageCache = new HashMap<>();//Create a cache for the images of the films

    public List<Pelicula> getPendingFilms() {
        return pendingFilms;
    }

    public List<Pelicula> getCompletedFilms() {
        return completedFilms;
    }

    public void addFilm(Pelicula film, MultipartFile imageFile, String listType) throws IOException {
        String folder = "src/main/resources/static/images/";//Create a folder for the images
        byte[] bytes = imageFile.getBytes();//Get the bytes of the image
        Path path = Paths.get(folder + imageFile.getOriginalFilename());//Create a path for the image
        Files.write(path, bytes);//Write the bytes to the path

        imageCache.put(imageFile.getOriginalFilename(), bytes);

        film.setImagePath(imageFile.getOriginalFilename());//Save the image path 
        if ("pending".equals(listType)) {//add the film by the list type
            pendingFilms.add(film);
        } else if ("completed".equals(listType)) {
            completedFilms.add(film);
        }
    }
    public byte[] getImage(String imageName) {
        return imageCache.get(imageName);
    }

    public void deleteFilm(String title, String listType) {//Delete a film from the list
        List<Pelicula> films = "pending".equals(listType) ? pendingFilms : completedFilms;//Choose the list to delete the film
        for (Pelicula film : films) {
            if (film.getTitle().equals(title)) {
                // Delete the image from the disk
                Path imagePath = Paths.get("src/main/resources/static" + film.getImagePath());
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Remove the film from the list
                films.remove(film);
                break;
            }
        }
    }
}