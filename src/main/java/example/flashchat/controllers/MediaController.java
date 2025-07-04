package example.flashchat.controllers;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import example.flashchat.Utils;
import example.flashchat.models.Media;
import example.flashchat.models.Post;
import example.flashchat.services.MediaService;
import example.flashchat.services.PostService;


@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private PostService postService;

    @PostMapping
    public boolean addMedia(@RequestParam String postId, @RequestParam MultipartFile file) {

        if (file == null || postId.isEmpty()) {
            // Empty checks
            return false;
        }

        if (!postService.postExists(postId)) {
            // Post does not exist
            return false;
        }

        Post post = postService.retrievePostById(postId);
        final String filePath = Utils.getFilePath(file.getOriginalFilename());

        Media media = new Media();
        media.setPost(post);
        media.setFilePath(filePath);

        if (mediaService.addMedia(media)) {
            // Save file to disk
            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                // TODO: Log error
                mediaService.deleteMedia(media.getId());
                e.printStackTrace();

                return false;
            }
        }
        else {
            return false;
        }

        return true;
    }


    @DeleteMapping
    public boolean deleteMedia(@RequestParam String mediaId) {
        if (mediaId == null) {
            // Empty check
            return false;
        }

        if (!mediaService.mediaExists(mediaId)) {
            // Media does not exist
            return false;
        }

        return mediaService.deleteMedia(mediaId);
    }


    @GetMapping
    public Media getMedia(@RequestParam String mediaId) {
        if (mediaId == null) {
            // Empty check
            return null;
        }

        if (!mediaService.mediaExists(mediaId)) {
            // Media does not exist
            return null;
        }

        Media m = mediaService.getMedia(mediaId);

        return m;
    }
}
