package org.dwsproject.proyectodesarrolloweb.Controllers;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Service.ImageService;
import org.dwsproject.proyectodesarrolloweb.Service.PostService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class ApiPostController {

    private final PostService postService;

    private final UserSession userSession;

    private final ImageService imageService;

    private final UserService userService;

    public ApiPostController(PostService postService, UserSession userSession, ImageService imageService, UserService userService) {
        this.postService = postService;
        this.userSession = userSession;
        this.imageService = imageService;
        this.userService = userService;
    }

    @GetMapping("/allPosts")
    public ResponseEntity<List<Post>> showPosts() {
        List <Post> posts = new ArrayList<>(postService.findAll());
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/newPost")
    public ResponseEntity<Post> newPost(Post post, @RequestParam(required = false) MultipartFile image, @RequestParam String username) throws IOException {
        User user = userService.findUserByUsername(username);

        if (user == null) { //If the user does not exist, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        post.setUser(user);
        if (image != null && !image.isEmpty()) { //If the image is not empty, save the image
            Image newImage = imageService.createImage(image);
            Image savedImage = imageService.saveImage(newImage);
            post.setImageId(savedImage.getId());
        }
        postService.sanitizeAndSavePost(post);
        userSession.incNumPosts();

        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> showPost(@PathVariable long id) {
        Post post = postService.findById(id);

        if (post != null) {
            return new ResponseEntity<>(post, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable long id) {
        if (postService.findById(id) == null) { //If the post does not exist, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        postService.deleteById(id);
        imageService.deleteImage(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> editPost(Post post, @RequestParam(required = false) MultipartFile image, @PathVariable long id) throws IOException {
        Post originalPost = postService.findById(id);

        if (originalPost == null) { //If the post does not exist, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        post.setUser(originalPost.getUser());
        postService.editById(post, id);

        if (image != null && !image.isEmpty()) { //If the image is not empty, save the image
            Image newImage = imageService.createImage(image);
            Image savedImage = imageService.saveImage(newImage);
            post.setImageId(savedImage.getId());
        }
        
        return new ResponseEntity<>(post, HttpStatus.OK);
    }
}
