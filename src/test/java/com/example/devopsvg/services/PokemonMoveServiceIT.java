package com.example.devopsvg.services;

import com.example.devopsvg.repos.PokemonMoveRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PokemonMoveServiceIT {
    @Autowired
    PokemonMoveRepo pokemonMoveRepo;
    @Autowired
    PokemonMoveService pokemonMoveService;

    @BeforeEach
    void setUp(){pokemonMoveRepo.deleteAll();}

    @Test
    void saveMoveToDatabaseIfItDoesNotAlreadyExistShouldSaveMove(){
        pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist("tackle");

        Assertions.assertEquals(1, pokemonMoveRepo.findAll().size());
    }
}
