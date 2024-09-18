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
import utils.JsonTestUtils;

import java.util.List;


@SpringBootTest
class PokemonServiceTest {
    @MockBean
    PokemonRepo pokemonRepo;
    @Autowired
    PokemonService pokemonService;
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();

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
    void createPokemonFromJsonShouldCreateCorrectPokemon() {
        Pokemon pokemon = pokemonService.createPokemonFromJson(
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

        Assertions.assertEquals(pokemonRepo.findByPokedexId(secondPokemonId),
                pokemonService.findNextPokemonInPokeDex(pokemonRepo.findByPokedexId(firstPokemonId)));
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

        Assertions.assertEquals(pokemonRepo.findByPokedexId(firstPokemonId),
                pokemonService.findPreviousPokemonInPokeDex(pokemonRepo.findByPokedexId(secondPokemonId)));
    }

}