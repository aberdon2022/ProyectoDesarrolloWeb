package org.dwsproject.proyectodesarrolloweb.Service;

import java.io.IOException;

import org.apache.tika.Tika;
import org.dwsproject.proyectodesarrolloweb.Classes.Image;
import org.dwsproject.proyectodesarrolloweb.Repositories.ImageRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image createImage(MultipartFile imageFile) throws IOException {
        // Verify the image file with Apache Tika
        Tika tika = new Tika();
        String type = tika.detect(imageFile.getBytes());
        MediaType mediaType = MediaType.parseMediaType(type);

        if (!mediaType.equals(MediaType.IMAGE_JPEG) && !mediaType.equals(MediaType.IMAGE_PNG)) {
            throw new IllegalArgumentException("Invalid image file type");
        }

        if (!imageFile.isEmpty()) {
            Image image = new Image();
            image.setData(imageFile.getBytes());
            image.setOriginalImageName(imageFile.getOriginalFilename());
            return image;
        }
        return null; // Return null if no image file is provided, or it's empty
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