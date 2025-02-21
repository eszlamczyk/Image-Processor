package pl.ernest.imageprocesor.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MiniatureConfig {
    private final int miniatureSize;

    public MiniatureConfig(@Value("${image.miniatureSize}") int miniatureSize){
        this.miniatureSize = miniatureSize;
    }
}
