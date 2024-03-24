package org.dwsproject.proyectodesarrolloweb.Classes;
import jakarta.persistence.*;


@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String title;
    private String text;
    boolean isOwner;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post() {
    }

    public Post(String username, String title, String text, boolean isOwner) {
        super();
        this.username = username;
        this.title = title;
        this.text = text;
        this.isOwner = isOwner;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return username;
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public void setUser(String user) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Post [id="+id+", user=" + user + ", title=" + title + ", text=" + text + "]";
    }

}
