package org.dwsproject.proyectodesarrolloweb.Classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Trailer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public String getFilePath() {
        return filePath;
    }

    public String getDescription() {
        return description;
    }

    @Column(name = "hash")
    @JsonIgnore
    private String hash;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "description")
    private String description;

    @Column(name = "title")
    private String title;

    public Trailer(String filePath, String originalFileName, String description, String title) {
        this.filePath = filePath;
        this.originalFileName = originalFileName;
        this.description = description;
        this.title = title;
    }

    public Trailer() {
    }

    public Long getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }
}