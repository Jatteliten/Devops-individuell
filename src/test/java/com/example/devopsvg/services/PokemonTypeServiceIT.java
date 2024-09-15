package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.javapoet.TypeName;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class PokemonTypeServiceIT {
    @Autowired
    PokemonTypeRepo pokemonTypeRepo;
    @Autowired
    PokemonTypeService pokemonTypeService;

    private final ObjectMapper objectMapper = new ObjectMapper();


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
    void saveTypeShouldSaveCorrectTypeRelationships(){
        String weaknessName = "fire";
        String resistName = "water";
        String typeName = "grass";

        try {
            objectMapper.readTree(
                            new String(
                                    Files.readAllBytes(Paths.get("src/test/resources/types.json"))))
                    .forEach(typeData ->
                            pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                                    typeData.path("name").asText()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pokemonTypeRepo.findAll().forEach(type ->{
            type.setHalfDamageFrom(new ArrayList<>());
            type.setDoubleDamageFrom(new ArrayList<>());
            type.setNoDamageFrom(new ArrayList<>());
            pokemonTypeRepo.save(type);
        });

        pokemonTypeService.addTypeRelationships();
        List<PokemonType> weaknesses = pokemonTypeRepo.findByName(typeName).getDoubleDamageFrom();
        List<PokemonType> strengths = pokemonTypeRepo.findByName(typeName).getHalfDamageFrom();

        Assertions.assertTrue(weaknesses.contains(pokemonTypeRepo.findByName(weaknessName)));
        Assertions.assertTrue(strengths.contains(pokemonTypeRepo.findByName(resistName)));
        Assertions.assertFalse(strengths.contains(pokemonTypeRepo.findByName(weaknessName)));
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