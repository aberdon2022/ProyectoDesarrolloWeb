package org.dwsproject.proyectodesarrolloweb.service;

import java.io.IOException;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public Image createImage(MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = new Image();
            image.setData(imageFile.getBytes());
            return image;
        }
        return null; // Return null if no image file is provided or it's empty
    }

    public Image saveImage (Image image) {
        return imageRepository.save(image);
    }

    public void deleteImage (long id) {
        imageRepository.deleteById(id);
    }

    public Image getImage (long id) {
        return imageRepository.findById(id).orElse(null);
    }

    public ByteArrayResource getImageAsResource(long id) {
        Image image = getImage(id);
        if (image != null) {
            return new ByteArrayResource(image.getData());
        } else {
            return null;
        }
    }
}