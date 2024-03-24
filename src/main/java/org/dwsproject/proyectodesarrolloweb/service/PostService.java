package org.dwsproject.proyectodesarrolloweb.service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    public Collection<Post> findAllByUserId(long userId) {//Return all the posts by user id
        List<Post> posts = postRepository.findAll();
        posts.removeIf(post -> post.getUser().getId() != userId);
        return posts;
    }

    public List<Post> findAll() {//Return all the posts
        return postRepository.findAll();
    }

    public Post findById(long id) {//Find a post by id
        return postRepository.findById(id).orElse(null);
    }

    public void savePost(Post post) {//Save a post with an id
        postRepository.save(post);
    }

    public void deleteById(long id) {//Delete a post by id
        postRepository.deleteById(id);
    }
    
    public void editById(Post post, long id){//Edit a post by id
        postRepository.save(post);
    }

}