package org.dwsproject.proyectodesarrolloweb.service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.dwsproject.proyectodesarrolloweb.Post;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private ConcurrentMap<Long, Post> posts = new ConcurrentHashMap<>();//Create a map to store the posts
    private AtomicLong nextId = new AtomicLong();//Create an id for the posts

    public PostService() {//When the service is created, create fake posts
        save(new Post("Taylor", "Opinion Clueless", "Perfect"));
        save(new Post("Swift", "Opinion El padrino", "Horrible"));
    }

    public Collection<Post> findAll() {//Return all the posts
        return posts.values();
    }

    public Post findById(long id) {//Find a post by id
        return posts.get(id);
    }

    public void save(Post post) {//Save a post with an id 

        long id = nextId.getAndIncrement();

        post.setId(id);

        this.posts.put(id, post);
    }

    public void deleteById(long id) {//Delete a post by id
        this.posts.remove(id);
    }
    public void editById(Post post, long id){//Edit a post by id
        if (posts.containsKey(id)) {
            posts.replace(id, post);
        } else {
            throw new IllegalArgumentException("There is no post with that ID: " + id);
        }

    }

}