package org.dwsproject.proyectodesarrolloweb.service;

import java.util.List;
import java.util.Objects;

import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Repositories.PostRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAllByUserId(String username) {//Return all the posts by user id
        List<Post> posts = postRepository.findAll();
        posts.removeIf(post -> !Objects.equals(post.getUser().getUsername(), username));
        return posts;
    }

    public List<Post> findAll() {//Return all the posts
        return postRepository.findAll();
    }

    public Post findById(long id) {//Find a post by id
        return postRepository.findById(id).orElse(null);
    }

    public void sanitizeAndSavePost(Post post) {//Save a post with an id
        String sanitizedText = Jsoup.clean(post.getText(), Safelist.basic());
        post.setText(sanitizedText);
        postRepository.save(post);
    }

    public void savePost(Post post) {
        List<Post> existingPosts = postRepository.findByTitleAndText(post.getTitle(), post.getText());
        if (existingPosts.isEmpty()) {
            postRepository.save(post);
        } else {
            // This post already exists!
            // Handle accordingly, perhaps throw an exception, or return the existing post
            throw new RuntimeException("Post with this title and text already exists!");
        }
    }

    public List<Post> findByTitleAndText(String title, String text) {
        return postRepository.findByTitleAndText(title, text);
    }

    public void deleteById(long id) {//Delete a post by id
        postRepository.deleteById(id);
    }

    public void editById(Post post, long id) {//Edit a post by id
        String sanitizedText = Jsoup.clean(post.getText(), Safelist.basic());
        post.setText(sanitizedText);
        postRepository.save(post);
    }

}