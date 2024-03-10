package org.dwsproject.proyectodesarrolloweb.Classes;
public class Pelicula {
    private String title;
    private int year;
    private String imagePath;
    private int rating;

    public Pelicula() {
    }
    public Pelicula(String title, int year, String imagePath, int rating) {
        this.title = title;
        this.year = year;
        this.imagePath = imagePath;
        this.rating = rating;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = "/images/" + imagePath;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
