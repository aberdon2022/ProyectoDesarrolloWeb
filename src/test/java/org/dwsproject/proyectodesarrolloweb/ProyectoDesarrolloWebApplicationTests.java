package org.dwsproject.proyectodesarrolloweb;

import org.dwsproject.proyectodesarrolloweb.Classes.Trailer;
import org.dwsproject.proyectodesarrolloweb.Service.TrailerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootTest
class ProyectoDesarrolloWebApplicationTests {

    @Autowired
    private TrailerService trailerService;

    @Test
    void contextLoads() {
        try {
            File file = new ClassPathResource("LateNightwiththeDevilTrailer.mp4").getFile();
            MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file.getName(), "video/mp4", file.toURI().toURL().openStream());

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(mockMultipartFile.getBytes()); // Update the MessageDigest object with the bytes of the file
            byte[] digest = md.digest(); // Generate the hash value
            String hash = Base64.getEncoder().encodeToString(digest); // Encode the hash value to a Base64 string

            Trailer trailer = new Trailer("","LateNightwiththeDevilTrailer.mp4","A trailer for the movie Late Night with the Devil","Late Night with the Devil");
            trailer.setHash(hash);

            trailerService.saveExampleTrailer(trailer, mockMultipartFile);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
