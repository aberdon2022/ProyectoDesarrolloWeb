package org.dwsproject.proyectodesarrolloweb;
import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.service.PostService;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class LoadDatabase {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PostService postService;

    public LoadDatabase(UserRepository userRepository, PostService postService, UserService userService) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.userService = userService;
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            saveUserIfNotExists("user1", "1");
            saveUserIfNotExists("user2", "2");
            saveUserIfNotExists("user3", "3");
            saveUserIfNotExists("user4", "4");

            // Save posts only after ensuring users exist
            Post post1 = new Post("Opinion Clueless", "Perfecta", userService.findUserByUsername("user3"));
            post1.setImageId(null); // explicitly set imageId to null
            savePostIfNotExists(post1);

            Post post2 = new Post("Opinion El padrino", "Horrible", userService.findUserByUsername("user4"));
            post2.setImageId(null); // explicitly set imageId to null
            savePostIfNotExists(post2);
        };
    }

    private void saveUserIfNotExists(String username, String password) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            userRepository.save(new User(username, password));
        }
    }

    private void savePostIfNotExists(Post post) {
        List<Post> existingPosts = postService.findAll();
        boolean postExists = existingPosts.stream().anyMatch(existingPost ->
                existingPost.getUser().getUsername().equals(post.getUser().getUsername()) &&
                        existingPost.getTitle().equals(post.getTitle()) &&
                        existingPost.getText().equals(post.getText())
        );
        if (!postExists) {
            postService.savePost(post);
        }
    }
}

