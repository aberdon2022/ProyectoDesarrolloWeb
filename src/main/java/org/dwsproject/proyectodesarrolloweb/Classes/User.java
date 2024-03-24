package org.dwsproject.proyectodesarrolloweb.Classes;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIdentityInfo( //Break the infinite recursion
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private String password;


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Film> pendingFilms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Film> completedFilms;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<User> friends;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    public User() {
        this.friends = new ArrayList<>();
        this.pendingFilms = new ArrayList<>();
        this.completedFilms = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
        this.pendingFilms = new ArrayList<>();
        this.completedFilms = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public List<Film> getPendingFilms() {
        List<Film> pendingFilms = this.pendingFilms.stream()
                .filter(film -> film.getStatus() == Film.FilmStatus.PENDING)
                .collect(Collectors.toList());
        pendingFilms.forEach(film -> System.out.println("Pending film: " + film.getTitle()));
        return pendingFilms;
    }

    public List<Film> getCompletedFilms() {
        List<Film> completedFilms = this.completedFilms.stream()
                .filter(film -> film.getStatus() == Film.FilmStatus.COMPLETED)
                .collect(Collectors.toList());
        completedFilms.forEach(film -> System.out.println("Completed film: " + film.getTitle()));
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

    public List<User> getFriends() {
        return this.friends;
    }

    public void addFriend(User user) {
        this.friends.add(user);
    }

    public void deleteFriend(User user) {
        this.friends.remove(user);
    }
}
