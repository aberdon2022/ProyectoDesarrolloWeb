package org.dwsproject.proyectodesarrolloweb.Classes;
import jakarta.persistence.*;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB") // MEDIUMBLOB because if the image is too big, it wouldn't be able to be stored in the database
    private byte[] data;

    @Column(name = "original_image_name")
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

    public void setOriginalImageName(String originalImageName) {
        this.originalImageName = originalImageName;
    }
}
