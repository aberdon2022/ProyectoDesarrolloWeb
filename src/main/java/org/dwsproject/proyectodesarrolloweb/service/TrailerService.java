package org.dwsproject.proyectodesarrolloweb.Service;

import org.dwsproject.proyectodesarrolloweb.Classes.Trailer;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerDeletionException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerUploadException;
import org.dwsproject.proyectodesarrolloweb.Repositories.TrailerRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class TrailerService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/uploads"); // Create a folder to store the trailers
    private final TrailerRepository trailerRepository;

    public TrailerService(TrailerRepository trailerRepository) {
        this.trailerRepository = trailerRepository;
    }

    public boolean uploadTrailer(MultipartFile file, String title, String description, User user) throws IOException, TrailerUploadException {

        if (user == null || !user.getUsername().equals("admin")) {
            throw new TrailerUploadException("User Unauthorized. Please login as admin.");
        }

        if (title.isEmpty() || description.isEmpty() || file.isEmpty()) {
            throw new TrailerUploadException("The title, description, or file cannot be empty.");
        }

        if (!Objects.equals(file.getContentType(), "video/mp4")) {
            throw new TrailerUploadException("The uploaded file is not a video.");
        }

        Trailer trailer = new Trailer();
        trailer.setOriginalFileName(file.getOriginalFilename());
        trailer.setTitle(title);
        trailer.setDescription(description);
        Trailer savedTrailer = trailerRepository.save(trailer);  // assuming the save method returns the saved entity
        String filePath = "src/main/resources/static/uploads/" + "trailer-" + savedTrailer.getId() + ".mp4";
        savedTrailer.setFilePath(filePath);
        trailerRepository.save(savedTrailer);
        saveTrailer("uploads", savedTrailer.getId(), file);
        return true;
    }

    private Path createFilePath(long trailerId) { // Create a path for the trailer
        return TrailerService.FILES_FOLDER.resolve("trailer-" + trailerId + ".mp4"); // Ensure to use proper extension
    }

    public void saveTrailer(String folderName, Long trailerId, MultipartFile trailer) throws IOException {
        Files.createDirectories(FILES_FOLDER); // Create the folder if it does not exist

        // Create a new Path that includes a file separator
        Path newFile = createFilePath(trailerId);

        trailer.transferTo(newFile); // Transfer the trailer to the new path
    }

    public void saveExampleTrailer (Trailer trailer, MultipartFile file) throws IOException {// Save the trailer to the database
        Trailer savedTrailer = trailerRepository.save(trailer);

        Path newFile = createFilePath(savedTrailer.getId());
        file.transferTo(newFile);

        String filePath = "/static/uploads/trailer-" + savedTrailer.getId() + ".mp4";
        savedTrailer.setFilePath(filePath);
        trailerRepository.save(savedTrailer);
    }

    public ResponseEntity<Object> createResponseFromTrailer(String folderName, long trailerId) throws MalformedURLException { // Create a response from the trailer

        Path trailerPath = createFilePath(trailerId);

        Resource file = new UrlResource(trailerPath.toUri()); // Create a resource from the trailer path

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "video/mp4").body(file); // Use the correct MIME-type for your video files
    }

    public void deleteTrailer(String folder, User user, Long trailerId) throws IOException {
        // Check if user is admin
    if (user==null || !user.getUsername().equals("admin")) {
        throw new TrailerDeletionException("User Unauthorized. Please login as admin.");
    }

    // Delete a trailer from the folder
    Path trailerFile = createFilePath(trailerId);
    Files.deleteIfExists(trailerFile);

    // Delete the trailer record from the database
    trailerRepository.deleteById(trailerId);

    }
}