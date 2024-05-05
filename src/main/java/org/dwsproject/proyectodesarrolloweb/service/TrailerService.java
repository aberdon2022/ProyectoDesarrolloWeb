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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TrailerService {

    private static final Path FILES_FOLDER = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/uploads"); // Create a folder to store the trailers
    private final TrailerRepository trailerRepository;
    private final UserService userService;

    public TrailerService(TrailerRepository trailerRepository, UserService userService) {
        this.trailerRepository = trailerRepository;
        this.userService = userService;
    }

    public String sanitizeFileName(String originalFileName) {

        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío.");
        }

        int lastIndexOf = originalFileName.lastIndexOf("."); // Get the last index of the file extension
        String fileName = originalFileName.substring(0, lastIndexOf); // Get the file name without the extension
        String extension = originalFileName.substring(lastIndexOf+1); // Get the file extension

        if (fileName.matches(".*[^a-zA-Z0-9-].*")) {
            throw new IllegalArgumentException("El nombre del archivo contiene caracteres inválidos.");
        }
        if (extension.matches(".*[^a-zA-Z0-9-].*")) {
            throw new IllegalArgumentException("El nombre del archivo contiene caracteres inválidos.");
        }
        return originalFileName;
    }

    public boolean uploadTrailer(MultipartFile file, String title, String description, User user) throws IOException, TrailerUploadException, NoSuchAlgorithmException {

        if (user == null || !userService.isAdmin(user)) {
            throw new TrailerUploadException("User Unauthorized. Please login as admin.");
        }

        if (title.isEmpty() || description.isEmpty() || file.isEmpty()) {
            throw new TrailerUploadException("The title, description, or file cannot be empty.");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null) {
            throw new TrailerUploadException("Invalid trailer file name. Please upload a valid MP4 file.");
        }

        String sanitizedFileName = sanitizeFileName(originalFileName);

        Tika tika = new Tika();
        String type = tika.detect(file.getBytes());

        if (!type.equals("video/mp4") && !type.equals("video/quicktime")) {
            throw new TrailerUploadException("Invalid trailer file type. Please upload a valid MP4 file.");
        }

        MessageDigest md = MessageDigest.getInstance("MD5"); // Create a new MessageDigest object with the MD5 algorithm
        md.update(file.getBytes()); // Update the digest with the trailer file bytes
        byte[] digest = md.digest(); // Calculate the MD5 hash of the bytes in the trailer file

        StringBuilder sb = new StringBuilder(); // Create a new StringBuilder object

        for (byte b : digest) { // Iterate over the bytes in the digest
            sb.append(String.format("%02x", b)); // Append the byte to the StringBuilder object
        }

        String hash = sb.toString(); // Hex representation of the MD5 hash to a string

        Trailer existingTrailer = trailerRepository.findByHash(hash);
        if (existingTrailer != null) {
            throw new TrailerUploadException("Trailer already exists.");
        }


        Trailer trailer = new Trailer();
        trailer.setOriginalFileName(originalFileName);
        trailer.setTitle(title);
        trailer.setDescription(description);
        trailer.setHash(hash);
        Trailer savedTrailer = trailerRepository.save(trailer);  // assuming the save method returns the saved entity
        String filePath = "src/main/resources/static/uploads/" + sanitizedFileName;
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

        if (!Files.exists(trailerPath)) {
            throw new TrailerNotFoundException("Trailer with ID: " + trailerId + " does not exist.");
        }

        Resource file = new UrlResource(trailerPath.toUri()); // Create a resource from the trailer path

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "video/mp4").body(file); // MIME-type for video files
    }

    public void deleteTrailer(String folder, User user, Long trailerId) throws IOException, TrailerNotFoundException {
    // Check if user is admin
    if (user == null || !userService.isAdmin(user)) {
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