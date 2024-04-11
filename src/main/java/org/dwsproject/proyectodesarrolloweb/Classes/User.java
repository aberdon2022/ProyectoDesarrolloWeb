package org.dwsproject.proyectodesarrolloweb.Classes;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.*;

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

    @Column(name = "token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    @OneToMany(mappedBy = "user")
    @JsonView(Views.Public.class)
    private final List<Film> pendingFilms = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonView(Views.Public.class)
    private final List<Film> completedFilms = new ArrayList<>();

    @OneToMany(mappedBy = "user1")
    private final Set<Friendship> friendships = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private final List<Post> posts = new ArrayList<>();


    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = UUID.randomUUID().toString();
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

    public String getPassword() {
        return password;
    }

    public Set<Friendship> getFriends() {
        return friendships;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Film> getPendingFilms() {
        return pendingFilms;
    }

    public List<Film> getCompletedFilms() {
        return completedFilms;
    }

    public Set<Friendship> getFriendships() {
        return friendships;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPendingFilms(List<Film> pendingFilms) {
        this.pendingFilms.clear(); //Clear the list before adding the new elements
        if (pendingFilms != null) {
            this.pendingFilms.addAll(pendingFilms);
        }
    }

    public void setCompletedFilms(List<Film> completedFilms) {
        this.completedFilms.clear(); //Clear the list before adding the new elements
        if (completedFilms != null) {
            this.completedFilms.addAll(completedFilms);
        }
    }


    @Override
    public boolean equals(Object obj) { //Override the equals method to compare the objects by username
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
