package example.flashchat.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import example.flashchat.models.Media;
import example.flashchat.services.MediaService;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

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

    @GetMapping("/{mediaId}")
    public ResponseEntity<Resource> serveMedia(@PathVariable String mediaId) {
        if (!mediaService.mediaExists(mediaId)) {
            return ResponseEntity.notFound().build();
        }

        Media media = mediaService.getMedia(mediaId);
        File file = new File(media.getFilePath());

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new UrlResource(file.toURI());
            String contentType = Files.probeContentType(file.toPath());
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}


// TODO: Needs testing.