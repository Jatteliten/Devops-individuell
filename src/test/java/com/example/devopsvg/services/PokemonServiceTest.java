package com.example.devopsvg.services;

import com.example.devopsvg.dto.pokemonViews.PokemonListDto;
import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.model.PokemonType;
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
import java.util.List;


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
    void createPokemonFromJsonShouldCreateCorrectPokemon() {
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

}