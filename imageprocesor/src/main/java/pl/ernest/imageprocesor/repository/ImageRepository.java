package pl.ernest.imageprocesor.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.ernest.imageprocesor.db.ImageRecord;
import reactor.core.publisher.Mono;


public interface ImageRepository extends ReactiveCrudRepository<ImageRecord, Long> {

    @Query("TRUNCATE images;")
    Mono<Void> dukeNukem();
}
