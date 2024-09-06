package com.example.servingwebcontent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;


/**
 * Ficheiro que representa o WEB Content.
 * @author Henrique José Correia Brás n2021229812
 * @author Tiago Rafael Cardoso Santos n2021229679
 */

/**
 * Uma classe de aplicação para servir conteúdo web usando o Spring Boot.
 */
@SpringBootApplication
public class ServingWebContentApplication {

	/**
	 * O ponto de entrada para a aplicação.
	 *
	 * @param args Os argumentos de linha de comando passados para a aplicação.
	 */
    public static void main(String[] args) {
        SpringApplication.run(ServingWebContentApplication.class, args);
    }

}