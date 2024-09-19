package com.example.devopsvg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class DevopsVgApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(FetchPokemonData.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);

        SpringApplication.run(DevopsVgApplication.class, args);
    }

}
