package org.dwsproject.proyectodesarrolloweb.Repositories;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
    List<Film> findByUserIdAndStatus (Long userId, Film.FilmStatus status);
    List<Film> findByUserIdAndTitle (Long userId, String title);
}
