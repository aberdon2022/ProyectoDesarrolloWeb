package org.dwsproject.proyectodesarrolloweb.Repositories;

import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
