package org.dwsproject.proyectodesarrolloweb.Repositories;

import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.springframework.data.jpa.domain.Specification;
public class FilmSpecification {

    public static Specification<Film> isCompleted() {
        return (film, query, cb) -> cb.equal(film.get("status"), "COMPLETED");
    }

    public static Specification<Film> hasRatingBetween(int minRating, int maxRating) {
        return (film, query, cb) -> cb.between(film.get("rating"), minRating, maxRating);
    }

    public static Specification<Film> isOwnedByUser(User user) {
        return (film, query, cb) -> cb.equal(film.get("user"), user);
    }
    public static Specification<Film> hasStatus(Film.FilmStatus status) {
        return (film, query, cb) -> cb.equal(film.get("status"), status);
    }
}
