package tn.esprit.autoloc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
        title = "Gestion de foyer",
        description = "API REST de gestion de foyer",
        version = "1.0.0",
        termsOfService =  "https://www.esprit.tn",
        contact = @Contact(
                name = "Equipe ASI ii",
                url = "https://www.esprit.tn",
                email = "equipe.asi2@esprit.tn")))
@SpringBootApplication
public class AutolocApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutolocApiApplication.class, args);
    }

}