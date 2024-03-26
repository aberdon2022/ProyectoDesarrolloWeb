package org.dwsproject.proyectodesarrolloweb.Classes;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.util.*;
import java.util.stream.Collectors;

@JsonIdentityInfo( //Break the infinite recursion
        generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private String password;


    @OneToMany(mappedBy = "user")
    @JsonView(Views.Public.class)
    private final List<Film> pendingFilms = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonView(Views.Public.class)
    private final List<Film> completedFilms = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private final Set<User> friends = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private final List<Post> posts = new ArrayList<>();

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

    public boolean checkPassword (String password) {
        return this.getPassword().equals(password);
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void addFriend (User friend) {
        this.friends.add(friend);
    }

    public void deleteFriend (User friend) {
        this.friends.remove(friend);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return Objects.equals(username, user.username);
    }
}
