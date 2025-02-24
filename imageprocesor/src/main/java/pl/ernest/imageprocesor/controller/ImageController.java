package pl.ernest.imageprocesor.controller;


import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import pl.ernest.imageprocesor.service.ImageService;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("api/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public Flux<String> uploadFiles(@RequestPart("files") Flux<FilePart> files) {
        return files.flatMap(imageService::saveImages);
    }

    @GetMapping("/names")
    public Flux<String> getImageNames(){
        return imageService.getAllMiniatureNames();
    }

    @GetMapping
    public Flux<Resource> getImages(){
        return imageService.getAllMiniatures();
    }
}
