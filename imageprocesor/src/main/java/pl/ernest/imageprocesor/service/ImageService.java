package pl.ernest.imageprocesor.service;


import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.ernest.imageprocesor.config.PathConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;


@Service

public class ImageService {
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
                .flatMap(miniatureHandler::getMiniatureFromPath);
    }

    public Flux<String> getAllMiniatureNames(){
        return imageRecordHandler.getAllMiniaturesFromDB();
    }
}
