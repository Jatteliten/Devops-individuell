package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonMove;
import com.example.devopsvg.repos.PokemonMoveRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import utils.JsonTestUtils;

@SpringBootTest
class PokemonMoveServiceTest {
    @MockBean
    PokemonMoveRepo pokemonMoveRepo;
    @Autowired
    PokemonMoveService pokemonMoveService;
    JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Test
    void repoShouldReturnCorrectMoveWhenFindingByName() {
        String testName = "pound";
        String testClass = "physical";
        Mockito.when(pokemonMoveRepo.findByName(testName)).thenReturn(
                PokemonMove.builder()
                        .name(testName)
                        .damageClass(testClass)
                        .build());

        Assertions.assertEquals(testClass, pokemonMoveRepo.findByName(testName).getDamageClass());
    }
    @Test
    void createPokemonMoveFromJsonShouldCreateCorrectMove() {
        PokemonMove pokemonMove= pokemonMoveService.createMoveFromJson(
                jsonTestUtils.getJsonFromFile("src/test/resources/move-pound.json"));

        Assertions.assertEquals("pound", pokemonMove.getName());
    }

}