package com.example.devopsvg.services;

import com.example.devopsvg.repos.PokemonRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PokemonServiceTestIntegration {
    @Autowired
    PokemonRepo pokemonRepo;
    @Autowired
    PokemonService pokemonService;

    @BeforeEach
    void setUp() {
        pokemonRepo.deleteAll();
    }

    @Test
    void savePokemonToDatabaseShouldSavePokemon() {
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(1);

        Assertions.assertEquals(1, pokemonRepo.findAll().size());
    }

    @Test
    void savePokemonToDataBaseShouldSaveCorrectPokemon(){
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(1);

        Assertions.assertEquals(1, pokemonRepo.findByName("Bulbasaur").getPokedexId());
        Assertions.assertEquals("Bulbasaur", pokemonRepo.findByName("Bulbasaur").getName());
    }
}
