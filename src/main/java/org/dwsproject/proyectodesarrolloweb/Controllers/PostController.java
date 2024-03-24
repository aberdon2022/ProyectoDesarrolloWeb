package org.dwsproject.proyectodesarrolloweb.Controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.service.ImageService;
import org.dwsproject.proyectodesarrolloweb.service.PostService;
import org.dwsproject.proyectodesarrolloweb.service.UserService;
import org.dwsproject.proyectodesarrolloweb.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PostController {

    private static final String POSTS_FOLDER = "posts";//Create a folder for the posts
//Use the methods of the service PostService, UserSession and ImageService
    @Autowired
    private PostService postService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @GetMapping("/forum")//Show the actual posts
    public String showPosts(Model model, HttpSession session) {

        List<Post> posts = postService.findAll(); //Obtain all the posts
        posts.forEach(post -> post.setUser(userService.findUserById(post.getUserId())));

        model.addAttribute("posts", postService.findAll());
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
    public String newPost(Model model, @RequestParam String username, Post post, MultipartFile image) throws IOException {
        User user = userService.findUserByUsername(username);

        if (user != null) {
            post.setUser(user);
            postService.savePost(post);

            if (image != null && !image.isEmpty()) {
                imageService.saveImage(POSTS_FOLDER, post.getId(), image);
            }
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
        model.addAttribute("post", post);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("imageExists", imageService.imageExists(POSTS_FOLDER, id));
        return "showPost";
    }

    @GetMapping("/post/{id}/image")//Download the image of a post by its id
    public ResponseEntity<Object> downloadImage(@PathVariable int id) throws MalformedURLException {
        return imageService.createResponseFromImage(POSTS_FOLDER, id);
    }

    @GetMapping("/post/{id}/delete")//Delete a post by its id
    public String deletePost(Model model, @PathVariable long id) throws IOException {
        Post post = postService.findById(id);
        String loggedInUser = userSession.getUser().getUsername();

        if (post.getUser().getUsername().equals(loggedInUser)) {
            postService.deleteById(id);
            imageService.deleteImage(POSTS_FOLDER, id);
            return "deletedPost";
        } else {
            return "redirect:/error/403";
        }
    }
    @GetMapping("/post/{id}/edit")//Show the form to edit a post by its id
    public String editPost(Model model, @PathVariable long id) throws IOException {
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
    public String editPost(Model model, Post post, MultipartFile image) throws IOException {

        User loggedInUser = userSession.getUser();

        post.setUser(loggedInUser);
        postService.editById(post, post.getId());

        if (image != null && !image.isEmpty()) { //Save the image if it exists
            imageService.saveImage(POSTS_FOLDER, post.getId(), image);
        }
        model.addAttribute("post", post);
        return "redirect:/forum";
    }
}
