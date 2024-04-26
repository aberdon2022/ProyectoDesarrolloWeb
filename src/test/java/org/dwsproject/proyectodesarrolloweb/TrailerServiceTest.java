package org.dwsproject.proyectodesarrolloweb;


import org.dwsproject.proyectodesarrolloweb.Service.TrailerService;
import org.junit.jupiter.api.Test;

public class TrailerServiceTest {

    @Test
    public void testSanitizeFileName() {
        TrailerService trailerService = new TrailerService(null);
        String originalFileName = "CashBa00--ckTrailer.mp4";
        String sanitizedFileName = trailerService.sanitizeFileName(originalFileName);
        System.out.println(sanitizedFileName);
    }
}
