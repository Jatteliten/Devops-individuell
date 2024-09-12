package com.example.devopsvg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.Objects;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class DevopsVgApplication {

    public static void main(String[] args) {
        if(args.length == 0) {
            SpringApplication.run(DevopsVgApplication.class, args);
        }else if(Objects.equals(args[0], "fetch-types-and-pokemon")){
            SpringApplication application = new SpringApplication(FetchTypesAndMoveAndPokemonToDatabase.class);
            application.setWebApplicationType(WebApplicationType.NONE);
            application.run(args);
        }
    }

}
