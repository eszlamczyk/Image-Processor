package pl.ernest.imageprocesor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import pl.ernest.imageprocesor.db.Image;
import pl.ernest.imageprocesor.repository.ImageRepository;
import pl.ernest.imageprocesor.webClient.GreetingClient;

import java.time.Duration;
import java.util.List;

@SpringBootApplication
public class ImageProcesorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ImageProcesorApplication.class, args);
        GreetingClient greetingClient = context.getBean(GreetingClient.class);
        // We need to block for the content here or the JVM might exit before the message is logged
        System.out.println(">> message = " + greetingClient.getMessage().block());
    }

    @Bean
    public CommandLineRunner demo(ImageRepository repository) {

        return (args) -> {
            // save a few customers
            repository.saveAll(List.of(
                            new Image("test", "test2")))
                    .blockLast(Duration.ofSeconds(10));
        };
    }

}
