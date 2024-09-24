package com.example.devopsvg.services;

import com.example.devopsvg.dto.pokemonViews.PokemonListDto;
import com.example.devopsvg.dto.pokemonViews.PokemonNextOrPreviousDto;
import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import utils.JsonTestUtils;

import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
class PokemonServiceTest {
    @MockBean
    PokemonRepo pokemonRepo;
    @Autowired
    PokemonService pokemonService;
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

    @Test
    void getAllPokemonInPokedexOrderShouldGetAllPokemon() {
        Pokemon pokemonOne = Pokemon.builder().name("testOne").build();
        Pokemon pokemonTwo = Pokemon.builder().name("testTwo").build();
        List<Pokemon> pokemonList = List.of(pokemonOne, pokemonTwo);
        Mockito.when(pokemonRepo.findAllByOrderByPokedexIdAsc()).thenReturn(pokemonList);

        Assertions.assertEquals(2, pokemonService.getAllPokemonInPokedexOrder().size());
        Assertions.assertTrue(pokemonService.getAllPokemonInPokedexOrder().contains(pokemonOne));
        Assertions.assertTrue(pokemonService.getAllPokemonInPokedexOrder().contains(pokemonTwo));
    }

    @Test
    void getPokemonByNameShouldReturnCorrectPokemon() {
        String name = "test";
        Pokemon pokemonOne = Pokemon.builder().name(name).build();

        Mockito.when(pokemonRepo.findByName(name)).thenReturn(pokemonOne);

        Assertions.assertEquals(pokemonOne, pokemonService.getPokemonByName(name));
    }

    @Test
    void getAllPokemonWithGivenTypeShouldReturnAllPokemonWithGivenType() {
        String name = "test";
        Pokemon pokemonOne = Pokemon.builder().name("testOne").build();
        Pokemon pokemonTwo = Pokemon.builder().name("testTwo").build();

        Mockito.when(pokemonRepo.findAllByTypes_Name(name)).thenReturn(List.of(pokemonOne, pokemonTwo));

        Assertions.assertEquals(2, pokemonService.getAllPokemonWithGivenType(name).size());
        Assertions.assertTrue(pokemonService.getAllPokemonWithGivenType(name).contains(pokemonOne));
        Assertions.assertTrue(pokemonService.getAllPokemonWithGivenType(name).contains(pokemonTwo));
    }

    @Test
    void getPokemonByPokedexIdShouldReturnCorrectPokemon() {
        int pokedexId = 1;
        Pokemon pokemon = Pokemon.builder().pokedexId(pokedexId).build();

        Mockito.when(pokemonRepo.findByPokedexId(pokedexId)).thenReturn(pokemon);

        Assertions.assertEquals(pokemon, pokemonService.getPokemonByPokedexId(pokedexId));
    }

    @Test
    void getAllPokemonWithNameThatContainsStringShouldReturnCorrectPokemon() {
        String findString = "te";
        Pokemon pokemonOne = Pokemon.builder().name("testOne").build();
        Pokemon pokemonTwo = Pokemon.builder().name("testTwo").build();

        Mockito.when(pokemonRepo.findAllByNameIsContainingIgnoreCase(findString))
                .thenReturn(List.of(pokemonOne, pokemonTwo));

        Assertions.assertEquals(2, pokemonService.getAllPokemonWithNameThatContainsString(findString).size());
        Assertions.assertTrue(pokemonService.getAllPokemonWithNameThatContainsString(findString).contains(pokemonOne));
        Assertions.assertTrue(pokemonService.getAllPokemonWithNameThatContainsString(findString).contains(pokemonTwo));
    }

    @Test
    void countNumberOfPokemonInDatabaseShouldCountCorrectAmountOfPokemon() {
        Long test = 3L;
        Mockito.when(pokemonRepo.count()).thenReturn(test);

        Assertions.assertEquals(test, pokemonService.countNumberOfPokemonInDatabase());
    }

    @Test
    void capitaliseFirstLetterShouldCapitaliseFirstLetter(){
        Assertions.assertEquals("Hej", pokemonService.capitalizeFirstLetter("hej"));
    }

    @Test
    void createPokemonFromJsonShouldCreateCorrectPokemon() {
        Pokemon pokemon = pokemonService.createPokemonFromJsonNode(
                jsonTestUtils.getJsonFromFile("src/test/resources/bulbasaur.json"));

        Assertions.assertEquals("Bulbasaur", pokemon.getName());
        Assertions.assertEquals(1, pokemon.getPokedexId());
    }

    @Test
    void convertPokemonToPokemonListDtoShouldConvertCorrectly(){
        String name = "bulbasaur";
        int id = 1;
        String spriteLink = "fakeLink.com";
        String type = "fakeType";
        PokemonType pokemonType = PokemonType.builder()
                .name(type).build();

        Pokemon pokemon = Pokemon.builder()
                .name(name)
                .pokedexId(id)
                .spriteLink(spriteLink)
                .types(List.of(pokemonType))
                .build();

        PokemonListDto pokemonListDto = pokemonService.convertPokemonToPokemonListDto(pokemon);

        Assertions.assertEquals(name, pokemonListDto.getName());
        Assertions.assertEquals(id, pokemonListDto.getPokedexId());
        Assertions.assertEquals(spriteLink, pokemonListDto.getSpriteLink());
        Assertions.assertEquals(type, pokemonListDto.getTypes().get(0).getName());
    }

    @Test
    void convertPokemonToPokemonNextOrPreviousDtoShouldConvertCorrectly(){
        Pokemon pokemon = Pokemon.builder()
                .name("testName")
                .spriteLink("testLink")
                .build();

        PokemonNextOrPreviousDto pokemonDto = pokemonService.convertPokemonToPokemonNextOrPreviousDto(pokemon);

        Assertions.assertEquals(pokemon.getName(), pokemonDto.getName());
        Assertions.assertEquals(pokemon.getSpriteLink(), pokemonDto.getSpriteLink());
    }

    @Test
    void findNextPokemonInPokeDexShouldReturnNextPokemon(){
        int firstPokemonId = 1;
        int secondPokemonId = 2;
        Mockito.when(pokemonRepo.findByPokedexId(firstPokemonId)).thenReturn(
                Pokemon.builder()
                        .pokedexId(firstPokemonId)
                        .build());
        Mockito.when(pokemonRepo.findByPokedexId(secondPokemonId)).thenReturn(
                Pokemon.builder()
                        .pokedexId(secondPokemonId)
                        .build());

        Assertions.assertEquals(pokemonService.getPokemonByPokedexId(secondPokemonId),
                pokemonService.findNextPokemonInPokeDex(pokemonService.getPokemonByPokedexId(firstPokemonId)));
    }

    @Test
    void findPreviousPokemonInPokeDexShouldReturnPreviousPokemon(){
        int firstPokemonId = 1;
        int secondPokemonId = 2;
        Mockito.when(pokemonRepo.findByPokedexId(firstPokemonId)).thenReturn(
                Pokemon.builder()
                        .pokedexId(firstPokemonId)
                        .build());
        Mockito.when(pokemonRepo.findByPokedexId(secondPokemonId)).thenReturn(
                Pokemon.builder()
                        .pokedexId(secondPokemonId)
                        .build());

        Assertions.assertEquals(pokemonService.getPokemonByPokedexId(firstPokemonId),
                pokemonService.findPreviousPokemonInPokeDex(pokemonService.getPokemonByPokedexId(secondPokemonId)));
    }

    @Test
    void getAllPokemonByTypeForListShouldReturnCorrectPokemon() {
        Pokemon pokemon = Pokemon.builder().name("test").build();
        PokemonListDto pokemonListDto = pokemonService.convertPokemonToPokemonListDto(pokemon);

        Mockito.when(pokemonRepo.findAllByTypes_Name("grass")).thenReturn(List.of(pokemon));

        Assertions.assertEquals(pokemonService.getAllPokemonByTypeForList("grass"), List.of(pokemonListDto));
    }

    @Test
    void removeLineBreaksAndFormFeedCharactersFromFlavorTextShouldConvertTextCorrectly() {
        String text = "This\u000cis\na\ntest";
        String expectedText = "This is a test";

        Assertions.assertEquals(pokemonService.removeLineBreaksAndFormFeedCharactersFromFlavorText(text), expectedText);
    }
}