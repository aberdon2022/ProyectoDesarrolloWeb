package org.dwsproject.proyectodesarrolloweb;
public class Pelicula {
    private String title;
    private int year;
    private String imagePath;

    public Pelicula() {
    }
    public Pelicula(String title, int year, String imagePath) {
        this.title = title;
        this.year = year;
        this.imagePath = imagePath;
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
}
