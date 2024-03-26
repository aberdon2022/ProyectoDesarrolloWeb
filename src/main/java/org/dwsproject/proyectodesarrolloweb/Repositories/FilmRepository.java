package org.dwsproject.proyectodesarrolloweb.Repositories;
import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
}
