package org.dwsproject.proyectodesarrolloweb.Controllers;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Security.jwt.UserLoginService;
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
    private final UserLoginService userLoginService;

    public ApiPostController(PostService postService, UserSession userSession, ImageService imageService, UserService userService, UserLoginService userLoginService) {
        this.postService = postService;
        this.userSession = userSession;
        this.imageService = imageService;
        this.userService = userService;
        this.userLoginService = userLoginService;
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
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
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
    public ResponseEntity<Void> deletePost(@PathVariable long id, @RequestParam String username) {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken) && !userService.isAdmin(userSession.getUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Post post = postService.findById(id);
        User user = userService.findUserByUsername(username);

        if (post == null || user == null) { //If the post does not exist, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!post.getUser().getUsername().equals(username)) { //If the user is not the owner of the post, return 403
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (postService.findById(id) == null) { //If the post does not exist, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        postService.deleteById(id);
        imageService.deleteImage(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> editPost(Post post, @RequestParam(required = false) MultipartFile image, @PathVariable long id, @RequestParam String username) throws IOException {
        //get username from token
        String usernameFromToken =userLoginService.getUserName() ;
        //if username is null response with unauthorized
        if(usernameFromToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // verify if username from token is the request username
        if (!username.equals(usernameFromToken) && !userService.isAdmin(userSession.getUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Post originalPost = postService.findById(id);
        User user = userService.findUserByUsername(username);

        if (originalPost == null || user == null) { //If the post does not exist, return 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!originalPost.getUser().getUsername().equals(username)) { //If the user is not the owner of the post, return 403
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
