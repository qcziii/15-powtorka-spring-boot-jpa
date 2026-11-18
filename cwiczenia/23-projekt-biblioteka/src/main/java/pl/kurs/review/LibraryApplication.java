package pl.kurs.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Library project: write everything else yourself (entities, repositories, services, controllers, DTOs,
 * error handling). Put your classes in pl.kurs.review or its subpackages. The API is described in ZADANIE.md.
 */
@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
