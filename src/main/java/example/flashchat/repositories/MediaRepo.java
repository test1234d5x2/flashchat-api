package example.flashchat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import example.flashchat.models.Media;

public interface MediaRepo extends JpaRepository<Media, String> {

}
