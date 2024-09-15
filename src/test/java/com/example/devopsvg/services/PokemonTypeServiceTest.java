package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import utils.JsonTestUtils;

import java.util.List;


@SpringBootTest
class PokemonTypeServiceTest {

    @MockBean
    PokemonTypeRepo pokemonTypeRepo;
    @Autowired
    PokemonTypeService pokemonTypeService;
    JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Test
    void repoShouldReturnCorrectTypeWhenFindingByName() {
        String testName = "grass";
        Mockito.when(pokemonTypeRepo.findByName(testName)).thenReturn(PokemonType.builder().name(testName).build());

        Assertions.assertEquals("grass", pokemonTypeRepo.findByName(testName).getName());
    }

    @Test
    void getTypesListFromApiShouldReturnCorrectAmountOfTypes(){
        List<PokemonType> actualList;
            actualList = pokemonTypeService.getPokemonTypesListFromApi(
                    jsonTestUtils.getJsonFromFile("src/test/resources/bulbasaur.json"));
        Assertions.assertEquals(2, actualList.size());
    }

}