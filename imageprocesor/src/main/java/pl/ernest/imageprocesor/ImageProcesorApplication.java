package pl.ernest.imageprocesor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.ernest.imageprocesor.webClient.GreetingClient;


@SpringBootApplication
public class ImageProcesorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ImageProcesorApplication.class, args);
        GreetingClient greetingClient = context.getBean(GreetingClient.class);
        // We need to block for the content here or the JVM might exit before the message is logged

        System.out.println(">> message = " + greetingClient.getMessage().block());
    }

}
