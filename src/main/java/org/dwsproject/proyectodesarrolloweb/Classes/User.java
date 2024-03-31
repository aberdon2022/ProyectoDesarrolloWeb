package org.dwsproject.proyectodesarrolloweb.Classes;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "user1")
    private final Set<Friendship> friendships = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private final List<Post> posts = new ArrayList<>();


    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

    public void addFriend (Friendship friendship) {
        this.friendships.add(friendship);
    }

    public void deleteFriend (Friendship friendship) {
        this.friendships.remove(friendship);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPendingFilms(List<Film> pendingFilms) {
        this.pendingFilms.clear();
        if (pendingFilms != null) {
            this.pendingFilms.addAll(pendingFilms);
        }
    }

    public void setCompletedFilms(List<Film> completedFilms) {
        this.completedFilms.clear();
        if (completedFilms != null) {
            this.completedFilms.addAll(completedFilms);
        }
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
