package pl.ernest.imageprocesor.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pl.ernest.imageprocesor.service.ImageRecordHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StartupClearConfig {

    @Autowired
    private PathConfig pathConfig;

    @Autowired
    private ImageRecordHandler imageRecordHandler;

    @PostConstruct
    public void clearOnStartup() {
        //clearDirectories();
        //imageRecordHandler.clearDB();
    }

    private void clearDirectories() {
        try {
            deleteDirectoryContents(pathConfig.getFullImagePath());
            deleteDirectoryContents(pathConfig.getMiniatureImagePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteDirectoryContents(String directoryPath) throws Exception {
        Path path = Paths.get(directoryPath);
        if (Files.exists(path) && Files.isDirectory(path)) {
            Files.walk(path)
                    .filter(p -> !p.equals(path))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

}
