package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class PokemonTypeServiceIT {
    @Autowired
    PokemonTypeRepo pokemonTypeRepo;
    @Autowired
    PokemonTypeService pokemonTypeService;


    @BeforeEach
    void setUp() {
        pokemonTypeRepo.deleteAll();
    }

    @Test
    @Transactional
    void saveTypeShouldSaveType(){
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("grass");

        Assertions.assertEquals(1, pokemonTypeRepo.findAll().size());
    }

    @Test
    @Transactional
    void getAllTypeNamesListShouldReturnAllTypeNames(){
        pokemonTypeRepo.save(PokemonType.builder().name("grass").build());
        pokemonTypeRepo.save(PokemonType.builder().name("poison").build());
        List<String> typeList = pokemonTypeService.getAllTypeNamesList();

        Assertions.assertEquals("grass", typeList.get(0));
        Assertions.assertEquals("poison", typeList.get(1));
    }

}