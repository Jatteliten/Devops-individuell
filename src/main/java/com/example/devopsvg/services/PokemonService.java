package com.example.devopsvg.services;

import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.repos.PokemonRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class PokemonService {

    private static final String POKE_API_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final int API_ALTERNATE_FORM_LIMITER = 5000;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PokemonRepo pokemonRepo;
    private final PokemonTypeService pokemonTypeService;
    private final PokemonMoveService pokemonMoveService;

    public PokemonService(RestTemplate restTemplate, ObjectMapper objectMapper,
                          PokemonRepo pokemonRepo, PokemonTypeService pokemonTypeService, PokemonMoveService pokemonMoveService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.pokemonRepo = pokemonRepo;
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonMoveService = pokemonMoveService;
    }

    public void savePokemonToDatabaseIfItDoesNotAlreadyExist(int pokemonId){
        JsonNode pokemonData = getPokemonDataFromApi(pokemonId);
        Optional<Pokemon> tempPokemon = Optional.ofNullable(
                pokemonRepo.findByName(pokemonData.path("name").asText()));
        if(tempPokemon.isEmpty() && pokemonData.path("id").asInt() < API_ALTERNATE_FORM_LIMITER){
            savePokemonToDatabase(pokemonData);
        }
    }

    private void savePokemonToDatabase(JsonNode pokemonData){
        Pokemon newPokemon = createPokemonFromJson(pokemonData);

        pokemonRepo.save(newPokemon);
        System.out.println(capitalizeFirstLetter(pokemonData.path("name").asText()) + " saved.");
    }

    public Pokemon createPokemonFromJson(JsonNode pokemonData) {
        return Pokemon.builder()
                .pokedexId(pokemonData.path("id").asInt())
                .name(capitalizeFirstLetter(pokemonData.path("name").asText()))
                .spriteLink(pokemonData.path("sprites")
                        .path("front_default")
                        .asText())
                .artworkLink(pokemonData.path("sprites")
                        .path("other")
                        .path("official-artwork")
                        .path("front_default")
                        .asText())
                .types(pokemonTypeService.getTypesListFromApi(pokemonData))
                .moves(pokemonMoveService.getMoveListFromApi(pokemonData))
                .build();
    }

    public JsonNode getPokemonDataFromApi(int pokemonId) {
        String url = POKE_API_URL + pokemonId;
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

}

