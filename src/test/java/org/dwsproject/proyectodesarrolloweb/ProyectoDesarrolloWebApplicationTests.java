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

@SpringBootTest
class ProyectoDesarrolloWebApplicationTests {

    @Autowired
    private TrailerService trailerService;

    @Test
    void contextLoads() {
        try {
            File file = new ClassPathResource("LateNightwiththeDevilTrailer.mp4").getFile();
            MockMultipartFile mockMultipartFile = new MockMultipartFile("file", file.getName(), "video/mp4", file.toURI().toURL().openStream());
            Trailer trailer = new Trailer("","LateNightwiththeDevilTrailer.mp4","A trailer for the movie Late Night with the Devil","Late Night with the Devil");
            trailerService.saveExampleTrailer(trailer, mockMultipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
