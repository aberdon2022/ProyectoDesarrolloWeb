package org.dwsproject.proyectodesarrolloweb.service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TrailerService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/uploads"); // Create a folder to store the trailers

    private Path createFilePath(long trailerId) { // Create a path for the trailer
        return TrailerService.FILES_FOLDER.resolve("trailer-" + trailerId + ".mp4"); // Ensure to use proper extension
    }

    public void saveTrailer(String folderName, long trailerId, MultipartFile trailer) throws IOException {
        Files.createDirectories(FILES_FOLDER); // Create the folder if it does not exist

        // Create a new Path that includes a file separator
        Path newFile = createFilePath(trailerId);

        trailer.transferTo(newFile); // Transfer the trailer to the new path
    }

    public ResponseEntity<Object> createResponseFromTrailer(String folderName, long trailerId) throws MalformedURLException { // Create a response from the trailer 

        Path trailerPath = createFilePath(trailerId);

        Resource file = new UrlResource(trailerPath.toUri()); // Create a resource from the trailer path 

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "video/mp4").body(file); // Use the correct MIME-type for your video files      
    }

    public void deleteTrailer(String folderName, long trailerId) throws IOException { // Delete a trailer from the folder 

        Path trailerFile = createFilePath(trailerId);

        Files.deleteIfExists(trailerFile);
    }

}