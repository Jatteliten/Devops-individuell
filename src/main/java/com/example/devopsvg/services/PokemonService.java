package com.example.devopsvg.services;

import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {

    private static final String POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PokemonRepo pokemonRepo;
    private final PokemonTypeService pokemonTypeService;

    public PokemonService(RestTemplate restTemplate, ObjectMapper objectMapper,
                          PokemonRepo pokemonRepo, PokemonTypeService pokemonTypeService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.pokemonRepo = pokemonRepo;
        this.pokemonTypeService = pokemonTypeService;
    }

    public void savePokemonToDatabaseIfItDoesNotAlreadyExist(int pokemonId){
        JsonNode pokemonData = getPokemonDataFromApi(pokemonId);
        Optional<Pokemon> tempPokemon = Optional.ofNullable(
                pokemonRepo.findByName(pokemonData.path("name").asText()));
        if(tempPokemon.isEmpty()){
            savePokemonToDatabase(pokemonData);
        }
    }

    private void savePokemonToDatabase(JsonNode pokemonData){
        Pokemon newPokemon = Pokemon.builder()
                .pokedexId(pokemonData.path("id").asInt())
                .name(pokemonData.path("name").asText())
                .spriteLink(pokemonData.path("sprites")
                        .path("front_default")
                        .asText())
                .artworkLink(pokemonData.path("sprites")
                        .path("other")
                        .path("official-artwork")
                        .path("front_default")
                        .asText())
                .types(getTypesListFromApi(pokemonData))
                .build();

        pokemonRepo.save(newPokemon);
        System.out.println(pokemonData.path("name").asText() + " saved.");
    }

    private List<PokemonType> getTypesListFromApi(JsonNode pokemonData){
        List<PokemonType> types = new ArrayList<>();
        for (JsonNode typeNode : pokemonData.path("types")) {
            types.add(pokemonTypeService.findPokemonTypeByName(
                    typeNode.path("type").path("name").asText()));
        }

        return types;
    }

    public JsonNode getPokemonDataFromApi(int pokemonId) {
        String url = POKEAPI_URL + pokemonId;
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

