package org.dwsproject.proyectodesarrolloweb;

import org.dwsproject.proyectodesarrolloweb.Classes.*;
import org.dwsproject.proyectodesarrolloweb.Repositories.FilmRepository;
import org.dwsproject.proyectodesarrolloweb.Repositories.ImageRepository;
import org.dwsproject.proyectodesarrolloweb.Repositories.RoleRepository;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.Service.FilmService;
import org.dwsproject.proyectodesarrolloweb.Service.ImageService;
import org.dwsproject.proyectodesarrolloweb.Service.PostService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

@Configuration
public class LoadDatabase implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PostService postService;
    private final PasswordEncoder passwordEncoder;
    private final FilmService filmService;
    private final FilmRepository filmRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    public LoadDatabase(UserRepository userRepository, RoleRepository roleRepository, PostService postService, UserService userService, PasswordEncoder passwordEncoder, FilmService filmService, FilmRepository filmRepository, ImageService imageService, ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.postService = postService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.filmService = filmService;
        this.filmRepository = filmRepository;
        this.imageService = imageService;
        this.imageRepository = imageRepository;
    }

    @Override
    public void run(String... args) throws IOException {
        //Initial Data

        User[] users = new User[]{
                new User("user1", passwordEncoder.encode("1")),
                new User("user2", passwordEncoder.encode("2")),
                new User("user3", passwordEncoder.encode("3")),
                new User("user4", passwordEncoder.encode("4")),
                new User("admin", passwordEncoder.encode("admin"))
        };

        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role("USER");
            roleRepository.save(userRole);
        }

        Role adminRole = roleRepository.findByName("ADMIN");
        if (adminRole == null) {
            adminRole = new Role("ADMIN");
            roleRepository.save(adminRole);
        }

        for (User user : users) {
            User existingUser = userRepository.findByUsername(user.getUsername());
            if (existingUser == null) {
                if (user.getUsername().equals("admin")) {
                    user.getRoles().add(userRole);
                    user.getRoles().add(adminRole);
                } else {
                    user.getRoles().add(userRole);
                }
                userRepository.save(user);
            }
        }

        Post[] posts = new Post[]{
                new Post("Opinion Clueless", "Perfecta", userService.findUserByUsername("user3")),
                new Post("Opinion El padrino", "Horrible", userService.findUserByUsername("user4"))
        };

        for (Post post : posts) {
            List<Post> existingPosts = postService.findByTitleAndText(post.getTitle(), post.getText());

            existingPosts.stream()
                    .filter(existingPost -> existingPost.getUser().getUsername().equals(post.getUser().getUsername()))
                    .findFirst()
                    .orElseGet(() -> {
                        postService.sanitizeAndSavePost(post);
                        return post;
                    });
        }
        long defaultImageId;
        String defaultImageName = "Clueless.jpg";
        Image defaultImage = imageRepository.findByOriginalImageName(defaultImageName);
        if (defaultImage == null) {
            ClassPathResource classPathResource = new ClassPathResource("static/images/" + defaultImageName);
            byte[] imageData = Files.readAllBytes(classPathResource.getFile().toPath());

            defaultImage = new Image();
            defaultImage.setData(imageData);
            defaultImage.setOriginalImageName("Clueless.jpg");
            defaultImage = imageService.saveImage(defaultImage);
            defaultImageId = defaultImage.getId();
        } else{
            defaultImageId = defaultImage.getId();
        }


        List<Film> existingFilm = filmRepository.findByUserIdAndTitle(1L, "Clueless");
        if (existingFilm.isEmpty()) {
            Film clueless = new Film("Clueless", 1995, 5);
            clueless.setFilmId(1L);
            clueless.setUser(userService.findUserByUsername("user1"));
            clueless.setStatus(Film.FilmStatus.COMPLETED);
            clueless.setPlot("Shallow, rich and socially successful Cher is at the top of her Beverly Hills high school's pecking scale. Seeing herself as a matchmaker, Cher first coaxes two teachers into dating each other.");
            clueless.setImageId(defaultImageId);
            filmRepository.save(clueless);

        }
    }
}

