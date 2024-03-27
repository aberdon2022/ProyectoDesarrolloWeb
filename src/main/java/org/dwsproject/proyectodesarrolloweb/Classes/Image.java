package org.dwsproject.proyectodesarrolloweb.Classes;
import jakarta.persistence.*;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] data;

    @Column(name = "originalImageName")
    private String originalImageName;

    public long getId() {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData (byte[] data) {
        this.data = data;
    }

    public String getOriginalImageName() {
        return originalImageName;
    }

    public void setOriginalImageName(String originalImageName) {
        this.originalImageName = originalImageName;
    }
}
