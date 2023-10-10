package me.universi.image.services;

import java.util.UUID;
import me.universi.image.entities.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import me.universi.image.repositories.ImageRepository;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public Image findFirstById(UUID id) {
        return imageRepository.findFirstById(id).orElse(null);
    }

    public Image save(Image image) {
        return imageRepository.saveAndFlush(image);
    }

}
