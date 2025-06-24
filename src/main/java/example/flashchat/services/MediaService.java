package example.flashchat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.flashchat.models.Media;
import example.flashchat.repositories.MediaRepo;

@Service
public class MediaService {

    @Autowired
    private MediaRepo mediaRepo;

    public boolean addMedia(Media media) {
        mediaRepo.save(media);
        return true;
    }

    public boolean deleteMedia(String mediaId) {
        mediaRepo.deleteById(mediaId);
        return true;
    }

    public boolean mediaExists(String mediaId) {
        return mediaRepo.existsById(mediaId);
    }

    public Media getMedia(String mediaId) {
        return mediaRepo.findById(mediaId).orElse(null);
    }

}
