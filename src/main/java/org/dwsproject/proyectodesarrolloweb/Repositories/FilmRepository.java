package org.dwsproject.proyectodesarrolloweb.Repositories;

import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {
    @Query("SELECT f FROM Film f WHERE f.status = 'COMPLETED' AND f.rating >= :minRating AND f.rating <= :maxRating AND f.user = :user")
    List <Film> findCompletedFilmsByRating(User user, int minRating, int maxRating);
}
