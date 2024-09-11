package com.example.devopsvg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

@SpringBootApplication
public class DevopsVgApplication {

    public static void main(String[] args) {
        if(args.length == 0) {
            SpringApplication.run(DevopsVgApplication.class, args);
        }else if(Objects.equals(args[0], "fetchtypesandpokemon")){
            SpringApplication application = new SpringApplication(FetchTypesAndPokemonFromDatabase.class);
            application.setWebApplicationType(WebApplicationType.NONE);
            application.run(args);
        }
    }

}
