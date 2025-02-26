package pl.ernest.imageprocesor.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.ernest.imageprocesor.config.PathConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;


@Service

public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final String full_image_path;

    private final MiniatureHandler miniatureHandler;

    private final ImageRecordHandler imageRecordHandler;

    public ImageService(PathConfig pathConfig, MiniatureHandler miniatureHandler, ImageRecordHandler imageRecordHandler) {
        full_image_path = pathConfig.getFullImagePath();
        this.miniatureHandler = miniatureHandler;
        this.imageRecordHandler = imageRecordHandler;
    }

    public Mono<String> saveImages(FilePart file) {
        return saveFullFile(file)
                .flatMap(this::saveFullFile)
                .flatMap(miniatureHandler::createMiniature)
                .flatMap(imageRecordHandler::saveFilePathsToDB);
    }

    private Mono<FilePart> saveFullFile(FilePart filePart) {
        Path destination = Path.of(full_image_path, filePart.filename());

        return filePart.transferTo(destination)
                .thenReturn(filePart);
    }

    public Flux<DataBuffer> getAllMiniatures(){
        return imageRecordHandler.getAllMiniaturesFromDB()
                .doOnSubscribe(sub -> logger.info("Fetching images from database..."))
                .doOnNext(image -> logger.info("Fetched image Path: {}", image))  // Log each image
                .flatMap(miniatureHandler::getMiniatureFromPath)
                .doOnComplete(() -> logger.info("Finished retrieving images."));
    }


    public Flux<String> getAllMiniatureNames(){
        return imageRecordHandler.getAllMiniaturesFromDB();
    }
}
