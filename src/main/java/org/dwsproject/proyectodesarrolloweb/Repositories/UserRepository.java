package org.dwsproject.proyectodesarrolloweb.Repositories;

import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAll();

    User findByToken(String token);
}
