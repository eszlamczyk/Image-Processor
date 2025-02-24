package pl.ernest.imageprocesor.service;

import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import pl.ernest.imageprocesor.config.MiniatureConfig;
import pl.ernest.imageprocesor.config.PathConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;


@Component
public class MiniatureHandler {

    private final int miniatureSize;

    private final String miniatureDirectory;

    public MiniatureHandler(MiniatureConfig miniatureConfig, PathConfig pathConfig){
        this.miniatureSize = miniatureConfig.getMiniatureSize();
        this.miniatureDirectory = pathConfig.getMiniatureImagePath();
    }

    public Mono<Tuple2<String, String>> createMiniature(FilePart filePart){
        return filePart.content()
                .reduce(new ByteArrayOutputStream(), (outputStream, buffer) -> {
                    try{
                        //todo: look into this
                        outputStream.write(buffer.asByteBuffer().array());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return outputStream;
                })
                .map(ByteArrayOutputStream::toByteArray)
                .flatMap(bytes -> {
                    try {
                        //todo: block unsupported formats
                        String format = getFormatName(filePart.filename()).orElse("jpg");
                        byte[] resizedImage = resize(new ByteArrayInputStream(bytes), miniatureSize);
                        return saveFile(resizedImage, filePart.filename() + "_miniature.jpeg")
                                .map(savedFilename -> Tuples.of(filePart.filename(), savedFilename));
                    } catch (IOException e){
                        return Mono.error(e);
                    }
                });
    }

    private byte[] resize(InputStream inputStream, int size) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, size, size, null);
        g2d.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpeg", outputStream); // Always write as JPEG
        return outputStream.toByteArray();
    }

    private Optional<String> getFormatName(String filename) {
        if (filename == null) return Optional.empty();
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif", "bmp", "tiff" -> Optional.of(extension);
            default -> Optional.empty();
        };
    }

    private Mono<String> saveFile(byte[] data, String fileName) {
        return Mono.fromCallable(() -> {
                    Files.createDirectories(Path.of(miniatureDirectory));
                    Path filePath = Path.of(miniatureDirectory, fileName);
                    Files.write(filePath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    return fileName;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<DataBuffer> getMiniatureFromPath(String filePath){
        try {
            Path file = Path.of(filePath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOf);
    }

}
