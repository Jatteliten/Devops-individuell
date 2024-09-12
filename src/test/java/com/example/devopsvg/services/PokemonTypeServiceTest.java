package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
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
import java.util.List;


@SpringBootTest
class PokemonTypeServiceTest {

    @MockBean
    PokemonTypeRepo pokemonTypeRepo;
    @Autowired
    PokemonTypeService pokemonTypeService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void repoShouldReturnCorrectTypeWhenFindingByName() {
        String testName = "grass";
        Mockito.when(pokemonTypeRepo.findByName(testName)).thenReturn(PokemonType.builder().name(testName).build());

        Assertions.assertEquals("grass", pokemonTypeRepo.findByName(testName).getName());
    }

    @Test
    void getTypesListFromApiShouldReturnCorrectAmountOfTypes(){
        List<PokemonType> actualList;
        try {
            actualList = pokemonTypeService.getTypesListFromApi(objectMapper.readTree(
                    new String(
                            Files.readAllBytes(Paths.get("src/test/resources/bulbasaur.json")))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(2, actualList.size());
    }

}