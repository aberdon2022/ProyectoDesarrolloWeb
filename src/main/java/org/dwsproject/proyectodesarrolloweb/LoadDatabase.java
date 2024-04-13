package org.dwsproject.proyectodesarrolloweb;

import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Classes.Role;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.RoleRepository;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.Service.PostService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class LoadDatabase implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PostService postService;
    private final PasswordEncoder passwordEncoder;

    public LoadDatabase(UserRepository userRepository, RoleRepository roleRepository, PostService postService, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.postService = postService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
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

    }
}

