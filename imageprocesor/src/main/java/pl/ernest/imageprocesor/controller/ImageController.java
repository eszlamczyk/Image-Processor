package pl.ernest.imageprocesor.controller;


import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import pl.ernest.imageprocesor.service.ImageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("api/images")
@AllArgsConstructor
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private static final String BOUNDARY = "--boundary\r\n";

    private final ImageService imageService;

    @PostMapping("/upload")
    public Flux<String> uploadFiles(@RequestPart("files") Flux<FilePart> files) {
        return files.flatMap(imageService::saveImages);
    }

    @GetMapping("/names")
    public Flux<String> getImageNames(){
        return imageService.getAllMiniatureNames();
    }

    @GetMapping(produces = MediaType.MULTIPART_MIXED_VALUE)
    public Flux<DataBuffer> getImages() {
        return imageService.getAllMiniatures()
                .flatMap(image -> Flux.concat(
                        Mono.just(toDataBuffer("--boundary--\r\nContent-Type: image/jpeg\r\n\r\n")),
                        Mono.just(image)
                                .doOnNext(buf -> logger.info("Sending image chunk of {} bytes", buf.readableByteCount())),
                        Mono.just(toDataBuffer("\r\n"))
                ))
                .concatWith(Mono.just(toDataBuffer("--boundary--\r\n")))
                .doOnSubscribe(sub -> logger.info("Starting image stream..."))
                .doOnComplete(() -> logger.info("Completed image stream."));
    }


    private DataBuffer toDataBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return new DefaultDataBufferFactory().wrap(bytes);
    }

}
