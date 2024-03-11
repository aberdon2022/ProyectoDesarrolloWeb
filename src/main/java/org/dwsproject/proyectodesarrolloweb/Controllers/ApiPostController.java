package org.dwsproject.proyectodesarrolloweb.Controllers;
import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.service.ImageService;
import org.dwsproject.proyectodesarrolloweb.service.PostService;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.dwsproject.proyectodesarrolloweb.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class ApiPostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @GetMapping("/allPosts")
    public ResponseEntity<List<Post>> showPosts() {
        List <Post> posts = new ArrayList<>(postService.findAll());
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/newPost")
    public ResponseEntity<Post> newPost(Post post, @RequestParam(required = false) MultipartFile image, @RequestParam String username) throws IOException {
        User user = userService.findUserByUsername(username);

        if (user == null) { //If the user does not exist, return 404
            return ResponseEntity.status(404).build();
        }

        post.setUser(user.getUsername());
        postService.save(post);

        if (image != null && !image.isEmpty()) { //If the image is not empty, save the image
            imageService.saveImage("posts", post.getId(), image);
        }

        userSession.incNumPosts();
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> showPost(@PathVariable long id) {
        Post post = postService.findById(id);

        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id) throws IOException {
        postService.deleteById(id);
        imageService.deleteImage("posts", id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> editPost(Post post, @RequestParam(required = false) MultipartFile image, @PathVariable long id) throws IOException {
        Post originalPost = postService.findById(id);

        if (originalPost == null) { //If the post does not exist, return 404
            return ResponseEntity.status(404).build();
        }

        post.setUser(originalPost.getUser());
        postService.editById(post, id);

        if (image != null && !image.isEmpty()) { //If the image is not empty, save the image
            imageService.saveImage("posts", id, image);
        }
        
        return ResponseEntity.ok(post);
    }
}
