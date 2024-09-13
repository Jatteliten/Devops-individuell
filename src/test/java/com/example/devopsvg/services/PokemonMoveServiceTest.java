package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonMove;
import com.example.devopsvg.repos.PokemonMoveRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class PokemonMoveServiceTest {
    @MockBean
    PokemonMoveRepo pokemonMoveRepo;
    @Autowired
    PokemonMoveService pokemonMoveService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void repoShouldReturnCorrectPokemonWhenFindingByName() {
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
    void createPokemonMoveFromJsonShouldCreateCorrectPokemon() {
        PokemonMove pokemonMove;

        try {
            pokemonMove = pokemonMoveService.createPokemonMoveFromJson(
                    objectMapper.readTree(
                            new String(
                                    Files.readAllBytes(Paths.get("src/test/resources/move-pound.json")))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals("pound", pokemonMove.getName());
    }

}