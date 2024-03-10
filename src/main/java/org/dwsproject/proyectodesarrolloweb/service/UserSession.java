package org.dwsproject.proyectodesarrolloweb.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class UserSession {//information about the actual user

    private String user;
    private int numPosts;

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public int getNumPosts() {//number of posts that the user has made
        return this.numPosts;
    }

    public void incNumPosts() {//when the user makes a post, the number of posts is increased
        this.numPosts++;
    }

}