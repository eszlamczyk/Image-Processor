package pl.ernest.imageprocesor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.ernest.imageprocesor.db.Image;


public interface ImageRepository extends ReactiveCrudRepository<Image, Long> {
}
