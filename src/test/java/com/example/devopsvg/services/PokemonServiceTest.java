package com.example.devopsvg.services;

import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.repos.PokemonRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class PokemonServiceTest {
    @MockBean
    PokemonRepo pokemonRepo;
    @Autowired
    PokemonService pokemonService;
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

}