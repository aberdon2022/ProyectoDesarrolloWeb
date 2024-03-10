package org.dwsproject.proyectodesarrolloweb.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    @Autowired
    private FilmService filmService;//use methods of the service FilmService

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "images");//Create a folder to store the images

    private Path createFilePath(long imageId, Path folder) {//Create a path for the image
        return folder.resolve("image-" + imageId + ".jpg");
    }

    public void saveImage(String folderName, long imageId, MultipartFile image) throws IOException {

        Path folder = FILES_FOLDER.resolve(folderName);//Create a folder for the images

        Files.createDirectories(folder);//Create the folder if it does not exist

        Path newFile = createFilePath(imageId, folder);//Create a path for the new image

        image.transferTo(newFile);//Transfer the image to the new path
    }

    public ResponseEntity<Object> createResponseFromImage(String folderName, long imageId) throws MalformedURLException {//Create a response from the image to show it in the web page

        Path folder = FILES_FOLDER.resolve(folderName);

        Path imagePath = createFilePath(imageId, folder);

        Resource file = new UrlResource(imagePath.toUri());//Create a resource from the image path 

        if(!Files.exists(imagePath)) {
            // If the image is not found on the disk, try to get it from memory
            byte[] image = filmService.getImage(folderName + "/" + imageId + ".jpg");
            if (image != null) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(file);
        }
    }

    public void deleteImage(String folderName, long imageId) throws IOException {//Delete an image from the folder 

        Path folder = FILES_FOLDER.resolve(folderName);

        Path imageFile = createFilePath(imageId, folder);

        Files.deleteIfExists(imageFile);
    }

}
