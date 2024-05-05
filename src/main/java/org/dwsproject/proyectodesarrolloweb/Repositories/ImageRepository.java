package org.dwsproject.proyectodesarrolloweb.Repositories;

import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByOriginalImageName(String original_image_name);
}
