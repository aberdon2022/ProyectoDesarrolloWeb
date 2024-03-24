package org.dwsproject.proyectodesarrolloweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ProyectoDesarrolloWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoDesarrolloWebApplication.class, args);
    }

}
