package org.dwsproject.proyectodesarrolloweb.Classes;
import jakarta.persistence.*;

@Entity
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long filmId;
    private String title;
    private int year;
    private int rating;
    private long imageId;

    @Enumerated(EnumType.STRING)
    private FilmStatus status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
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

    public long getImageId() { // Changed from getImagePath to getImageId
        return imageId; // Changed from imagePath to imageId
    }

    public void setImageId(long imageId) { // Changed from setImagePath to setImageId
        this.imageId = imageId; // Changed from imagePath to imageId
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}