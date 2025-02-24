package pl.ernest.imageprocesor.service;


import org.springframework.stereotype.Component;
import pl.ernest.imageprocesor.config.PathConfig;
import pl.ernest.imageprocesor.db.ImageRecord;
import pl.ernest.imageprocesor.repository.ImageRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class ImageRecordHandler {

    private final ImageRepository imageRepository;

    private final String fullImagePath;

    private final String miniatureImagePath;


    public ImageRecordHandler(ImageRepository imageRepository, PathConfig pathConfig) {
        this.imageRepository = imageRepository;
        this.fullImagePath = pathConfig.getFullImagePath();
        this.miniatureImagePath = pathConfig.getMiniatureImagePath();
    }

    public Mono<ImageRecord> saveImage(ImageRecord imageRecord) {
        return imageRepository.save(imageRecord)
                .delayElement(Duration.of(1, ChronoUnit.SECONDS));
    }

    public Mono<String> saveFilePathsToDB(Tuple2<String, String> filenames) {
        ImageRecord imageRecord = new ImageRecord(fullImagePath + filenames.getT1(),
                miniatureImagePath + filenames.getT2());

        return saveImage(imageRecord)
                .map(ImageRecord::getFullPath);
    }

    public Flux<String> getAllMiniaturesFromDB() {
        return imageRepository.findAll()
                .map(ImageRecord::getMiniaturePath)
                .delayElements(Duration.of(1, ChronoUnit.SECONDS));
    }

    public void clearDB() {
        try {
            imageRepository.dukeNukem().subscribe();
            System.out.println("Image records have been deleted from the database.");
        } catch (Exception e) {
            System.err.println("Error while clearing the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
