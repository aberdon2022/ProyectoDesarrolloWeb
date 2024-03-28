package org.dwsproject.proyectodesarrolloweb.Repositories;

import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleAndText(String title, String text);
}
