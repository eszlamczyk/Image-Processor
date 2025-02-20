package pl.ernest.imageprocesor.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class PathConfig {

    private final String fullImagePath;

    private final String miniatureImagePath;

    public PathConfig(@Value("${image.miniature.path}") String miniatureImagePath,
                      @Value("${image.full.path}") String fullImagePath)
            throws IOException {
        this.miniatureImagePath = miniatureImagePath;
        this.fullImagePath = fullImagePath;
        createFoldersIfNeeded();
    }

    public String getFullImagePath(){
        return fullImagePath;
    }

    public String getMiniatureImagePath(){
        return miniatureImagePath;
    }


    private void createFoldersIfNeeded() throws IOException {
        Files.createDirectories(Paths.get(fullImagePath));
        Files.createDirectories(Paths.get(miniatureImagePath));
    }

}
