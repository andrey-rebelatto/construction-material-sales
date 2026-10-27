package br.edu.materialconstrucao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    exclude =
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
            .class)
public class MaterialConstrucaoApplication {
  public static void main(String[] args) {
    SpringApplication.run(MaterialConstrucaoApplication.class, args);
  }
}
