package org.dwsproject.proyectodesarrolloweb;
import java.util.ArrayList;

public class User {
    private long id;
    private String username;
    private String password;
    private ArrayList<User> friends;

    public User() {
        this.friends = new ArrayList<>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
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
