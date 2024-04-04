package org.dwsproject.proyectodesarrolloweb.Controllers;

import jakarta.servlet.http.HttpSession;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Service.ImageService;
import org.dwsproject.proyectodesarrolloweb.Service.PostService;
import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class PostController {

    private final PostService postService;

    private final UserSession userSession;

    private final ImageService imageService;

    private final UserService userService;

    public PostController(PostService postService, UserSession userSession, ImageService imageService, UserService userService) {
        this.postService = postService;
        this.userSession = userSession;
        this.imageService = imageService;
        this.userService = userService;
    }

    @GetMapping("/forum")//Show the actual posts
    public String showPosts(Model model, HttpSession session, @RequestParam(value = "username", required = false) String username) {

        List<Post> posts;
        if (username != null && !username.isEmpty()) {
            User user = userService.findUserByUsername(username);
            if (user == null) {
                posts = postService.findAll();
                model.addAttribute("posts", posts);
                model.addAttribute("userNotFound", true);
                model.addAttribute("loggedInUser", userSession.getUser().getUsername());
                return "indexForum";
            }
            posts = postService.findAllByUserId(username);

        } else {
            posts = postService.findAll(); //Obtain all the posts
        }
        posts.forEach(post -> post.setUser(userService.findUserById(post.getUserId())));
        
        model.addAttribute("posts", posts);
        model.addAttribute("welcome", session.isNew());
        model.addAttribute("loggedInUser", userSession.getUser().getUsername());
        return "indexForum";
    }

    @GetMapping("/forum/new")//Show the form to add a new post
    public String newPostForm(Model model) {
        model.addAttribute("user", userSession.getUser());
        return "newPost";
    }

    @PostMapping("/forum/new")
    public String newPost(Model model, @RequestParam String username, Post post, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        User user = userService.findUserByUsername(username);
        userSession.validateUser(user.getUsername());

        if (user != null) {
            post.setUser(user);

            if (imageFile != null && !imageFile.isEmpty()) {
                Image newImage = imageService.createImage(imageFile);
                Image savedImage = imageService.saveImage(newImage);
                post.setImageId(savedImage.getId()); // set the image id in the post
            } else {
                post.setImageId(null); // set the image id in the post to null
            }
            postService.sanitizeAndSavePost(post);
            userSession.incNumPosts();
            model.addAttribute("numPosts", userSession.getNumPosts());
            return "redirect:/forum";
        } else {
            return "redirect:/error/401";
        }
    }

    @GetMapping("/post/{id}")//Show a post by its id
    public String showPost(Model model, @PathVariable long id) {
        //Obtain the post by its id
        Post post = postService.findById(id);
        String loggedInUser = userSession.getUser().getUsername();
        boolean isOwner = post.getUser().getUsername().equals(loggedInUser);
        boolean imageExists = post.getImageId() != null;
        model.addAttribute("post", post);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("imageExists", imageExists);
        return "showPost";
    }

    @GetMapping("/post/{id}/delete")//Delete a post by its id
    public String deletePost (@PathVariable long id) {
        userSession.validateUser(userSession.getUser().getUsername());
        Post post = postService.findById(id);
        String loggedInUser = userSession.getUser().getUsername();
        boolean isOwner = post.getUser().getUsername().equals(loggedInUser);
        if (isOwner) {
            Long ImageId = post.getImageId();
            postService.deleteById(id);
            if (ImageId != null) {
                imageService.deleteImage(ImageId);
            }
            return "deletedPost";
        } else {
            return "redirect:/error/403";
        }
    }
    @GetMapping("/post/{id}/edit")//Show the form to edit a post by its id
    public String editPost(Model model, @PathVariable long id) {
        userSession.validateUser(userSession.getUser().getUsername());
        Post post = postService.findById(id);
        String loggedInUser = userSession.getUser().getUsername();
        boolean isOwner = post.getUser().getUsername().equals(loggedInUser);
        if (isOwner) {
            model.addAttribute("post", post);
            return "editPost";
        }else {
            return "redirect:/error/401";
        }
    }

    @PostMapping("/post/{id}/edit")//Edit a post by its id
    public String editPost(Model model, Post post, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        User loggedInUser = userSession.getUser();

        post.setUser(loggedInUser);

        if (imageFile != null && !imageFile.isEmpty()) { // If a new image file is provided, save the new image
            Image newImage = imageService.createImage(imageFile);
            Image savedImage = imageService.saveImage(newImage);
            post.setImageId(savedImage.getId()); // Set the image id in the post to the id of the new image
        } else { // If a new image file is not provided, keep the existing image id
            Post existingPost = postService.findById(post.getId());
            post.setImageId(existingPost.getImageId());
        }

        postService.editById(post, post.getId());
        model.addAttribute("post", post);
        return "redirect:/forum";
    }
}