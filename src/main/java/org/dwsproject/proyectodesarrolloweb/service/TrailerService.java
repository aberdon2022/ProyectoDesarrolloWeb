package org.dwsproject.proyectodesarrolloweb.Service;

import org.apache.tika.Tika;
import org.dwsproject.proyectodesarrolloweb.Classes.Trailer;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerDeletionException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerNotFoundException;
import org.dwsproject.proyectodesarrolloweb.Exceptions.TrailerUploadException;
import org.dwsproject.proyectodesarrolloweb.Repositories.TrailerRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

        Tika tika = new Tika();
        String type = tika.detect(file.getBytes());

        if (!type.equals("video/mp4")) {
            throw new TrailerUploadException("Invalid trailer file type. Please upload a valid MP4 file.");
        }

        Trailer trailer = new Trailer();
        trailer.setOriginalFileName(file.getOriginalFilename());
        trailer.setTitle(title);
        trailer.setDescription(description);
        Trailer savedTrailer = trailerRepository.save(trailer);  // assuming the save method returns the saved entity
        String filePath = "src/main/resources/static/uploads/" + file.getOriginalFilename();
        savedTrailer.setFilePath(filePath);
        trailerRepository.save(savedTrailer);
        saveTrailer("uploads", file);
        return true;
    }

    private Path createFilePath(String originalFileName) { // Create a path for the trailer
        return TrailerService.FILES_FOLDER.resolve(originalFileName);
    }

    public void saveTrailer(String folderName, MultipartFile trailer) throws IOException {
        Files.createDirectories(FILES_FOLDER); // Create the folder if it does not exist

        // Create a new Path that includes a file separator
        Path newFile = createFilePath(trailer.getOriginalFilename());

        trailer.transferTo(newFile); // Transfer the trailer to the new path
    }

    //This method is used to save the example trailer
    public void saveExampleTrailer (Trailer trailer, MultipartFile file) throws IOException {// Save the trailer to the database
        Trailer savedTrailer = trailerRepository.save(trailer);

        Path newFile = createFilePath(file.getOriginalFilename());
        file.transferTo(newFile);

        String filePath = "/static/uploads/" + file.getOriginalFilename();
        savedTrailer.setFilePath(filePath);
        trailerRepository.save(savedTrailer);
    }

    public ResponseEntity<Object> createResponseFromTrailer(String folderName, long trailerId) throws MalformedURLException { // Create a response from the trailer

        Trailer trailer = trailerRepository.findById(trailerId).orElseThrow(() -> new TrailerNotFoundException("Trailer with ID: " + trailerId + " does not exist."));

        Path trailerPath = createFilePath(trailer.getOriginalFileName());

        Resource file = new UrlResource(trailerPath.toUri()); // Create a resource from the trailer path

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "video/mp4").body(file); // MIME-type for video files
    }

    public void deleteTrailer(String folder, User user, Long trailerId) throws IOException, TrailerNotFoundException {
    // Check if user is admin
    if (user == null || !user.getUsername().equals("admin")) {
        throw new TrailerDeletionException("User Unauthorized. Please login as admin.");
    }

    // Attempt to find the trailer in the database
    Optional<Trailer> trailer = trailerRepository.findById(trailerId);
    if (trailer.isEmpty()) {
        throw new TrailerNotFoundException("Trailer with ID: " + trailerId + " does not exist.");
    }

    // Delete a trailer from the folder
    Path trailerFile = createFilePath(trailer.get().getOriginalFileName());
    boolean isDeleted = Files.deleteIfExists(trailerFile);
    if (!isDeleted) {
        throw new IOException("Failed to delete trailer file for ID: " + trailerId);
    }

    // Delete the trailer record from the database
    trailerRepository.deleteById(trailerId);
}

    public List<Trailer> getAllTrailers() {
        return trailerRepository.findAll();
    }

    public Trailer searchById (Long id) throws TrailerNotFoundException {
        Optional<Trailer> trailer = trailerRepository.findById(id);
        if (trailer.isEmpty()) {
            throw new TrailerNotFoundException("Trailer with ID: " + id + " does not exist.");
        }
        return trailer.get();
    }

}