package com.example.devopsvg;

import com.example.devopsvg.services.PokemonMoveService;
import com.example.devopsvg.services.PokemonService;
import com.example.devopsvg.services.PokemonTypeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ComponentScan
public class FetchTypesAndMoveAndPokemonToDatabase implements CommandLineRunner {
    private final PokemonTypeService pokemonTypeService;
    private final PokemonService pokemonService;
    private final PokemonMoveService pokemonMoveService;
    private final ObjectMapper objectMapper;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String POKEMON_TYPES_LIST_API_URL = "https://pokeapi.co/api/v2/type";
    private static final String POKEMON_MOVES_LIST_API_URL = "https://pokeapi.co/api/v2/move?limit=10000&offset=0";
    private static final String POKEMON_LIST_API_URL = "https://pokeapi.co/api/v2/pokemon?limit=100000&offset=0";

    public FetchTypesAndMoveAndPokemonToDatabase(PokemonTypeService pokemonTypeService, PokemonService pokemonService,
                                                 PokemonMoveService pokemonMoveService, ObjectMapper objectMapper){
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonService = pokemonService;
        this.pokemonMoveService = pokemonMoveService;
        this.objectMapper = objectMapper;
    }
    @Override
    public void run(String... args) throws Exception {
        fetchTypesToDatabase(createJsonNodeFromUrl(POKEMON_TYPES_LIST_API_URL));
        fetchMovesToDatabase(createJsonNodeFromUrl(POKEMON_MOVES_LIST_API_URL));
        fetchPokemonToDatabase(createJsonNodeFromUrl(POKEMON_LIST_API_URL));
        System.out.println("*** Fetch complete ***");
    }

    private void fetchTypesToDatabase(JsonNode resultsNode) {
        for (JsonNode pokemonTypeNode : resultsNode) {
            pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                    pokemonTypeNode.path("name").asText());
        }
        System.out.println("*** All types added ***");
    }

    private void fetchPokemonToDatabase(JsonNode resultsNode) {
        for (JsonNode pokemonNode : resultsNode) {
            pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(
                    extractIdFromUrl(pokemonNode.path("url").asText()));
        }
        System.out.println("*** All Pokémon added ***");
    }

    private void fetchMovesToDatabase(JsonNode resultsNode) {
        for (JsonNode pokemonNode : resultsNode) {
            pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist(
                    extractIdFromUrl(pokemonNode.path("url").asText()));
        }
        System.out.println("*** All moves added ***");
    }

    private JsonNode createJsonNodeFromUrl(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode rootNode = objectMapper.readTree(response.body());
        return rootNode.path("results");
    }

    private int extractIdFromUrl(String url) {
        url = url.substring(0, url.length() - 1);

        int lastSlashIndex = url.lastIndexOf("/");

        return Integer.parseInt(url.substring(lastSlashIndex + 1));
    }
}
