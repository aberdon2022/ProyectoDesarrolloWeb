package org.dwsproject.proyectodesarrolloweb.service;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class FilmService {


    private Map<String, byte[]> imageCache = new HashMap<>();//Create a cache for the images of the films

    public void addFilm(User user, Film film, MultipartFile imageFile, String listType) throws IOException {
        String folder = "src/main/resources/static/images/";//Create a folder for the images
        byte[] bytes = imageFile.getBytes();//Get the bytes of the image
        Path path = Paths.get(folder + imageFile.getOriginalFilename());//Create a path for the image
        Files.write(path, bytes);//Write the bytes to the path

        imageCache.put(imageFile.getOriginalFilename(), bytes);

        film.setImagePath(imageFile.getOriginalFilename());//Save the image path 
        if ("pending".equals(listType)) {//add the film by the list type
            user.getPendingFilms().add(film);
        } else if ("completed".equals(listType)) {
            user.getCompletedFilms().add(film);
        }
    }

    public byte[] getImage(String imageName) {
        return imageCache.get(imageName);
    }

    public void deleteFilm (User user, String title, String listType) {//Delete a film from the list
        List<Film> films = "pending".equals(listType) ? user.getPendingFilms() : user.getCompletedFilms();
        for (Film film : films) {
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