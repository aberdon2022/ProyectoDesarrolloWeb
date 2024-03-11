package org.dwsproject.proyectodesarrolloweb.Classes;

public class Post {

    private Long id;
    private String user;
    private String title;
    private String text;
    boolean isOwner;

    public Post() {
    }

    public Post(String user, String title, String text, boolean isOwner) {
        super();
        this.user = user;
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
        return user;
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public void setUser(String user) {
        this.user = user;
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
