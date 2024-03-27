package org.dwsproject.proyectodesarrolloweb.service;

import java.util.List;
import java.util.Objects;

import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Repositories.PostRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
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

    public void savePost(Post post) {//Save a post with an id
        String sanitizedText = Jsoup.clean(post.getText(), Safelist.basic());
        post.setText(sanitizedText);
        postRepository.save(post);
    }

    public void deleteById(long id) {//Delete a post by id
        postRepository.deleteById(id);
    }
    
    public void editById(Post post, long id){//Edit a post by id
        String sanitizedText = Jsoup.clean(post.getText(), Safelist.basic());
        post.setText(sanitizedText);
        postRepository.save(post);
    }

}