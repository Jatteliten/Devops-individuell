package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonMove;
import com.example.devopsvg.repos.PokemonMoveRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import utils.JsonTestUtils;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PokemonMoveServiceTest {
    @MockBean
    PokemonMoveRepo pokemonMoveRepo;
    @Autowired
    PokemonMoveService pokemonMoveService;
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Value("${filepath.pound.json}")
    private String poundFilePath;

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
                jsonTestUtils.getJsonFromFile(poundFilePath));

        Assertions.assertEquals("pound", pokemonMove.getName());
    }

}