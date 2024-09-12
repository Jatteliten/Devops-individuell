package com.example.devopsvg.services;

import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.repos.PokemonRepo;
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
class PokemonServiceTest {
    @MockBean
    PokemonRepo pokemonRepo;
    @Autowired
    PokemonService pokemonService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void repoShouldReturnCorrectPokemonWhenFindingByName() {
        String testName = "bulbasaur";
        int testId = 1;
        Mockito.when(pokemonRepo.findByName(testName)).thenReturn(
                Pokemon.builder()
                        .name(testName)
                        .pokedexId(testId)
                        .build());

        Assertions.assertEquals(testId, pokemonRepo.findByName(testName).getPokedexId());
    }

    @Test
    void capitaliseFirstLetterShouldCapitaliseFirstLetter(){
        Assertions.assertEquals("Hej", pokemonService.capitalizeFirstLetter("hej"));
    }

    @Test
    void createPokemonFromJsonShouldCreateCorrectPokemon(){
        Pokemon pokemon;

        try {
            pokemon = pokemonService.createPokemonFromJson(
                    objectMapper.readTree(
                            new String(
                                    Files.readAllBytes(Paths.get("src/test/resources/bulbasaur.json")))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals("Bulbasaur", pokemon.getName());
        Assertions.assertEquals(1, pokemon.getPokedexId());
    }

}