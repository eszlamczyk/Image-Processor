package pl.ernest.imageprocesor.service;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import pl.ernest.imageprocesor.config.MiniatureConfig;
import pl.ernest.imageprocesor.config.PathConfig;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
                        String format = getFormatName(filePart.filename()).orElse("jpg");
                        byte[] resizedImage = resize(new ByteArrayInputStream(bytes), miniatureSize, format);
                        return saveFile(resizedImage, filePart.filename() + "_miniature." + format)
                                .map(savedFilename -> Tuples.of(filePart.filename(), savedFilename));
                    } catch (IOException e){
                        return Mono.error(e);
                    }
                });
    }

    private byte[] resize(InputStream inputStream, int size, String format) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);
        BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, format, outputStream);
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

}
