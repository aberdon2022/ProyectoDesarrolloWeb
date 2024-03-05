package org.dwsproject.proyectodesarrolloweb.service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.dwsproject.proyectodesarrolloweb.Post;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private ConcurrentMap<Long, Post> posts = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong();

    public PostService() {
        save(new Post("Taylor", "Opinión Clueless", "Perfecta"));
        save(new Post("Swift", "Opinión El padrino", "Horrible"));
    }

    public Collection<Post> findAll() {
        return posts.values();
    }

    public Post findById(long id) {
        return posts.get(id);
    }

    public void save(Post post) {

        long id = nextId.getAndIncrement();

        post.setId(id);

        this.posts.put(id, post);
    }

    public void deleteById(long id) {
        this.posts.remove(id);
    }

}