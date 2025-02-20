package pl.ernest.imageprocesor.service;

import org.springframework.http.MediaType;
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

    private final String miniature_image_path;

    public ImageHandler(PathConfig pathConfig) {
        full_image_path = pathConfig.getFullImagePath();
        miniature_image_path = pathConfig.getMiniatureImagePath();
    }


    public Mono<ServerResponse> retrieveImage(ServerRequest request){
        return request.multipartData()
                .map(multipart -> multipart.get("files"))
                .defaultIfEmpty(Collections.emptyList())
                .flatMapMany(Flux::fromIterable)
                .cast(org.springframework.http.codec.multipart.FilePart.class)
                .flatMap(this::saveFile)
                .collectList()
                .flatMap(filenames -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .bodyValue("Uploaded: " + String.join(", ", filenames)));
    }

    private Mono<String> saveFile(org.springframework.http.codec.multipart.FilePart filePart) {
        Path destination = Path.of(full_image_path, filePart.filename());

        return filePart.transferTo(destination)
                .thenReturn(filePart.filename());
    }

}
