package com.example.devopsvg.services;

import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.repos.PokemonTypeRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PokemonServiceIT {
    @Autowired
    PokemonRepo pokemonRepo;
    @Autowired
    PokemonService pokemonService;
    @Autowired
    PokemonTypeService pokemonTypeService;
    private final String TEST_POKEMON_NAME = "Bulbasaur";

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

        Assertions.assertEquals(1, pokemonRepo.findByName(TEST_POKEMON_NAME).getPokedexId());
        Assertions.assertEquals(TEST_POKEMON_NAME, pokemonRepo.findByName(TEST_POKEMON_NAME).getName());
    }

    @Test
    void savePokemonToDataBaseShouldSaveCorrectTypes(){
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("grass");
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("poison");
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(1);

        Assertions.assertEquals(2, pokemonRepo.findByName(TEST_POKEMON_NAME).getTypes().size());
        Assertions.assertEquals("grass", pokemonRepo.findByName(TEST_POKEMON_NAME).getTypes().get(0).getName());
        Assertions.assertEquals("poison", pokemonRepo.findByName(TEST_POKEMON_NAME).getTypes().get(1).getName());
    }
}
