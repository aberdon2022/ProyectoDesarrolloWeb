package org.dwsproject.proyectodesarrolloweb.Classes;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    private LocalDateTime timestamp;

    public void setUser1(User user) {
        this.user1 = user;
    }

    public void setUser2(User user) {
        this.user2 = user;
    }

    public void setTimestamp(LocalDateTime now) {
        this.timestamp = now;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
