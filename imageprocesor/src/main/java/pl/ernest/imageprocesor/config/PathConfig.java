package pl.ernest.imageprocesor.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathConfig {
    @Value("${image.full.path}")
    private String fullImagePath;

    @Value("${image.miniature.path}")
    private String miniatureImagePath;

    public String getFullImagePath(){
        return fullImagePath;
    }

    public String getMiniatureImagePath(){
        return miniatureImagePath;
    }

}
