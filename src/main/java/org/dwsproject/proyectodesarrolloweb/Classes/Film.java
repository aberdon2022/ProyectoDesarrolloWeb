package org.dwsproject.proyectodesarrolloweb.Classes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class) // This field is visible in the public view of the API
    private long filmId;

    @JsonView(Views.Public.class)
    private String title;

    @JsonView(Views.Public.class)
    private int year;

    @JsonView(Views.Public.class)
    private int rating;

    @JsonView(Views.Public.class)
    private String plot;

    @Transient // This field is not stored in the database
    @JsonView(Views.Public.class)
    private List<String> ratingStars;

    public void setPlot(String plot) {
        this.plot = plot;
    }

    @Column (name = "image_id")
    private Long imageId;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private FilmStatus status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore // Break the infinite recursion
    private User user;

    public void setRatingStars(List<String> ratingStars) {
        this.ratingStars = ratingStars;
    }

    public enum FilmStatus {
        PENDING,
        COMPLETED
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

    public int getRating() {
        return rating;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId (long imageId) {
        this.imageId = imageId;
    }
}