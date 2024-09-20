package br.com.desafio;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "br.com.desafio.domain.repository")
@SpringBootApplication
public class SanGiorgioApplication {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        // Verifica se as variáveis estão sendo carregadas corretamente
        String accessKey = dotenv.get("AWS_ACCESS_KEY");
        String secretKey = dotenv.get("AWS_SECRET_KEY");

        if (accessKey == null || secretKey == null) {
            System.err.println("As variáveis de ambiente não foram carregadas corretamente!");
            return;
        }

        // Define as variáveis de ambiente no sistema
        System.setProperty("AWS_ACCESS_KEY", accessKey);
        System.setProperty("AWS_SECRET_KEY", secretKey);

        SpringApplication.run(SanGiorgioApplication.class, args);
    }
}