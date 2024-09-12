package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PokemonTypeServiceTestIntegration {
    @Autowired
    PokemonTypeRepo pokemonTypeRepo;
    @Autowired
    PokemonTypeService pokemonTypeService;


    @BeforeEach
    void setUp() {
        pokemonTypeRepo.deleteAll();
    }

    @Test
    void saveTypeShouldSaveType(){
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("grass");

        Assertions.assertEquals(1, pokemonTypeRepo.findAll().size());
    }

    @Test
    void getAllTypeNamesListShouldReturnAllTypeNames(){
        pokemonTypeRepo.save(PokemonType.builder().name("grass").build());
        pokemonTypeRepo.save(PokemonType.builder().name("poison").build());

        Assertions.assertEquals("grass", pokemonTypeService.getAllTypeNamesList().get(0));
        Assertions.assertEquals("poison", pokemonTypeService.getAllTypeNamesList().get(1));
    }

}