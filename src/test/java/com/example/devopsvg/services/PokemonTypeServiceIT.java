package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import utils.JsonTestUtils;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class PokemonTypeServiceIT {
    @Autowired
    PokemonTypeRepo pokemonTypeRepo;
    @Autowired
    PokemonTypeService pokemonTypeService;
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Value("${filepath.types.json}")
    private String typesFilePath;

    @BeforeEach
    void setUp() {
        pokemonTypeRepo.deleteAll();
    }

    @Test
    @Transactional
    void saveTypeShouldSaveType(){
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("grass");

        Assertions.assertEquals(1, pokemonTypeService.getAllPokemonTypes().size());
    }

    @Test
    @Transactional
    void saveTypeShouldSaveCorrectTypeRelationships(){
        String weaknessName = "fire";
        String resistName = "water";
        String typeName = "grass";

        jsonTestUtils.getJsonFromFile(typesFilePath)
                .forEach(typeData ->
                        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                                typeData.path("name").asText()));

        pokemonTypeService.getAllPokemonTypes().forEach(type ->{
            type.setHalfDamageFrom(new ArrayList<>());
            type.setDoubleDamageFrom(new ArrayList<>());
            type.setNoDamageFrom(new ArrayList<>());
            pokemonTypeRepo.save(type);
        });

        pokemonTypeService.addTypeRelationshipsIfTheyDoNotAlreadyExist();
        List<PokemonType> weaknesses = pokemonTypeService.getPokemonTypeByName(typeName).getDoubleDamageFrom();
        List<PokemonType> strengths = pokemonTypeService.getPokemonTypeByName(typeName).getHalfDamageFrom();

        Assertions.assertTrue(weaknesses.contains(pokemonTypeService.getPokemonTypeByName(weaknessName)));
        Assertions.assertTrue(strengths.contains(pokemonTypeService.getPokemonTypeByName(resistName)));
        Assertions.assertFalse(strengths.contains(pokemonTypeService.getPokemonTypeByName(weaknessName)));
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