package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import utils.JsonTestUtils;

import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class PokemonTypeServiceTest {

    @MockBean
    PokemonTypeRepo pokemonTypeRepo;
    @Autowired
    PokemonTypeService pokemonTypeService;
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Value("${filepath.bulbasaur.json}")
    private String bulbasaurFilePath;

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
                    jsonTestUtils.getJsonFromFile(bulbasaurFilePath));
        Assertions.assertEquals(2, actualList.size());
    }

}