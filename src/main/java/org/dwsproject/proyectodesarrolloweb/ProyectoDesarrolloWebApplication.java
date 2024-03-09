package org.dwsproject.proyectodesarrolloweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAsync
@SpringBootApplication
public class ProyectoDesarrolloWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoDesarrolloWebApplication.class, args);
    }

}
