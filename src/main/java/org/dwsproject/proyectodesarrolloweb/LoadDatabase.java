package org.dwsproject.proyectodesarrolloweb;
import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Classes.Trailer;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.Service.PostService;
import org.dwsproject.proyectodesarrolloweb.Service.TrailerService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class LoadDatabase implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PostService postService;

    public LoadDatabase(UserRepository userRepository, PostService postService, UserService userService) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        //Initial Data

        User[] users = new User[]{
                new User("user1", "1"),
                new User("user2", "2"),
                new User("user3", "3"),
                new User("user4", "4"),
                new User("admin", "admin")
        };

        for (User user : users) {
            User existingUser = userRepository.findByUsername(user.getUsername());
            if (existingUser == null) {
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
                        postService.savePost(post);
                        return post;
                    });
        }

    }
}

