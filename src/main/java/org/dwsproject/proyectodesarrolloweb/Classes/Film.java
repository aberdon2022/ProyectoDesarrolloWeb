package org.dwsproject.proyectodesarrolloweb.Classes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

@Entity
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    private long filmId;

    @JsonView(Views.Public.class)
    private String title;

    @JsonView(Views.Public.class)
    private int year;

    @JsonView(Views.Public.class)
    private int rating;

    @Column (name = "image_id")
    private Long imageId;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private FilmStatus status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore // Break the infinite recursion
    private User user;

    public enum FilmStatus {
        PENDING,
        COMPLETED
    }

    public FilmStatus getStatus() {
        return status;
    }

    public void setStatus(FilmStatus status) {
        this.status = status;
    }



    public Film() {
    }

    public Film(String title, int year, int rating) {
        this.title = title;
        this.year = year;
        this.rating = rating;
    }

    public long getFilmId() {
        return filmId;
    }

    public void setFilmId(long id) {
        this.filmId = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId (long imageId) {
        this.imageId = imageId;
    }
}