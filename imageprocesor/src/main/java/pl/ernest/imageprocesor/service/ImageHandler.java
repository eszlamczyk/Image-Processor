package pl.ernest.imageprocesor.service;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.ernest.imageprocesor.config.PathConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Collections;

@Component
public class ImageHandler {

    private final String full_image_path;

    private final MiniatureHandler miniatureHandler;

    private final ImageRecordHandler imageRecordHandler;

    public ImageHandler(PathConfig pathConfig, MiniatureHandler miniatureHandler, ImageRecordHandler imageRecordHandler) {
        full_image_path = pathConfig.getFullImagePath();
        this.miniatureHandler = miniatureHandler;
        this.imageRecordHandler = imageRecordHandler;
    }


    public Mono<ServerResponse> retrieveImage(ServerRequest request){
        return request.multipartData()
                .map(multipart -> multipart.get("files"))
                .defaultIfEmpty(Collections.emptyList())
                .flatMapMany(Flux::fromIterable)
                .cast(FilePart.class)
                .flatMap(this::saveFullFile)
                .flatMap(miniatureHandler::createMiniature)
                .flatMap(imageRecordHandler::saveFilePathsToDB)
                .collectList()
                .flatMap(filenames -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .bodyValue("Uploaded & Resized: " + String.join(", ", filenames)));
    }


    private Mono<FilePart> saveFullFile(FilePart filePart) {
        Path destination = Path.of(full_image_path, filePart.filename());

        return filePart.transferTo(destination)
                .thenReturn(filePart);
    }

}
