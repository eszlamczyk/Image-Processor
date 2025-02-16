package pl.ernest.imageprocesor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.ernest.imageprocesor.db.ImageRecord;


public interface ImageRepository extends ReactiveCrudRepository<ImageRecord, Long> {
}
