package org.dwsproject.proyectodesarrolloweb.Classes;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo( //Break the infinite recursion
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")

public class User {
    private long id;
    private String username;
    private String password;
    private ArrayList<User> friends;

    private List<Film> pendingFilms;
    private List<Film> completedFilms;

    public User() {
        this.friends = new ArrayList<>();
        this.pendingFilms = new ArrayList<>();
        this.completedFilms = new ArrayList<>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
        this.pendingFilms = new ArrayList<>();
        this.completedFilms = new ArrayList<>();
    }

    public List<Film> getPendingFilms() {
        return pendingFilms;
    }

    public List<Film> getCompletedFilms() {
        return completedFilms;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    protected String getPassword() {
        return password;
    }

    public boolean checkPassword(String password) {
        return this.getPassword().equals(password);
    }

    public ArrayList<User> getFriends() {
        return this.friends;
    }

    public void addFriend(User user) {
        this.friends.add(user);
    }

    public void deleteFriend(User user) {
        this.friends.remove(user);
    }
}
