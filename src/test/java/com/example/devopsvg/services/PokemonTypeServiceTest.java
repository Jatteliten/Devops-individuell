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
    void getAllPokemonTypesShouldReturnAllPokemonTypes() {
        PokemonType typeOne = PokemonType.builder().name("typeOne").build();
        PokemonType typeTwo = PokemonType.builder().name("typeTwo").build();
        List<PokemonType> typeList = List.of(typeOne, typeTwo);

        Mockito.when(pokemonTypeRepo.findAll()).thenReturn(typeList);

        Assertions.assertEquals(2, pokemonTypeService.getAllPokemonTypes().size());
        Assertions.assertTrue(pokemonTypeService.getAllPokemonTypes().contains(typeOne));
        Assertions.assertTrue(pokemonTypeService.getAllPokemonTypes().contains(typeTwo));
    }

    @Test
    void getPokemonTypeByNameShouldReturnCorrectPokemonType() {
        String typeString = "typeOne";
        PokemonType typeOne = PokemonType.builder().name(typeString).build();

        Mockito.when(pokemonTypeRepo.findByName("typeOne")).thenReturn(typeOne);

        Assertions.assertEquals(typeOne, pokemonTypeService.getPokemonTypeByName(typeString));
    }

    @Test
    void countNumberOfTypesInDatabaseShouldCountTheCorrectAmountOfTypesInDatabase() {
        Long amount = 3L;
        Mockito.when(pokemonTypeRepo.count()).thenReturn(amount);

        Assertions.assertEquals(amount, pokemonTypeService.countNumberOfTypesInDatabase());
    }

    @Test
    void repoShouldReturnCorrectTypeWhenFindingByName() {
        String typeName = "test";
        PokemonType type = PokemonType.builder().name(typeName).build();

        Mockito.when(pokemonTypeRepo.findByName(typeName)).thenReturn(type);

        Assertions.assertEquals(type, pokemonTypeService.getPokemonTypeByName(typeName));
    }

    @Test
    void getTypesListFromApiShouldReturnCorrectAmountOfTypes(){
        List<PokemonType> actualList;
            actualList = pokemonTypeService.getPokemonTypesListFromPokemonEntryFromApi(
                    jsonTestUtils.getJsonFromFile(bulbasaurFilePath));

        Assertions.assertEquals(2, actualList.size());
    }

}