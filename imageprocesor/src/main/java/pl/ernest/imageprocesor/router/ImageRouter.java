package pl.ernest.imageprocesor.router;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.ernest.imageprocesor.service.ImageHandler;

@Configuration(proxyBeanMethods = false)
public class ImageRouter {

    @Bean
    public RouterFunction<ServerResponse> routeImages(ImageHandler imageHandler){
        return RouterFunctions.route()
                .POST("api/upload", imageHandler::retrieveImage)
                .build();
    }
}
