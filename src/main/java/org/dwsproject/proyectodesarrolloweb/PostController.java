package org.dwsproject.proyectodesarrolloweb;

import java.io.IOException;
import java.net.MalformedURLException;

import jakarta.servlet.http.HttpSession;

import org.dwsproject.proyectodesarrolloweb.service.ImageService;
import org.dwsproject.proyectodesarrolloweb.service.PostService;
import org.dwsproject.proyectodesarrolloweb.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PostController {

    private static final String POSTS_FOLDER = "posts";//Create a folder for the posts

    @Autowired
    private PostService postService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private ImageService imageService;

    @GetMapping("/forum")
    public String showPosts(Model model, HttpSession session) {

        model.addAttribute("posts", postService.findAll());
        model.addAttribute("welcome", session.isNew());

        return "indexForum";
    }

    @GetMapping("/forum/new")
    public String newPostForm(Model model) {

        model.addAttribute("user", userSession.getUser());

        return "newPost";
    }

    @PostMapping("/forum/new")
    public String newPost(Model model, Post post, MultipartFile image) throws IOException {

        postService.save(post);

        imageService.saveImage(POSTS_FOLDER, post.getId(), image);

        userSession.setUser(post.getUser());
        userSession.incNumPosts();

        model.addAttribute("numPosts", userSession.getNumPosts());

        return "savedPost";
    }

    @GetMapping("/post/{id}")
    public String showPost(Model model, @PathVariable long id) {
        //Obtain the post by its id
        Post post = postService.findById(id);
        model.addAttribute("post", post);

        return "showPost";
    }

    @GetMapping("/post/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable int id) throws MalformedURLException {

        return imageService.createResponseFromImage(POSTS_FOLDER, id);
    }

    @GetMapping("/post/{id}/delete")
    public String deletePost(Model model, @PathVariable long id) throws IOException {

        postService.deleteById(id);

        imageService.deleteImage(POSTS_FOLDER, id);

        return "deletedPost";
    }
    @GetMapping("/post/{id}/edit")
    public String editPost(Model model, @PathVariable long id) throws IOException {

        Post post = postService.findById(id);
        model.addAttribute("post", post);

        return "editPost";
    }
    @PostMapping("/post/{id}/edit")
    public String editPost(Model model, Post post, MultipartFile image) throws IOException {

        postService.editById(post, post.getId());

        imageService.saveImage(POSTS_FOLDER, post.getId(), image);
        model.addAttribute("post", post);

        return "redirect:/forum";
    }
}