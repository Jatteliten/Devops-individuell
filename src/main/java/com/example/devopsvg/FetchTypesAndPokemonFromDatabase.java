package com.example.devopsvg;

import com.example.devopsvg.services.PokemonService;
import com.example.devopsvg.services.PokemonTypeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ComponentScan
public class FetchTypesAndPokemonFromDatabase implements CommandLineRunner {
    private final PokemonTypeService pokemonTypeService;
    private final PokemonService pokemonService;
    private final ObjectMapper objectMapper;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String POKEMON_TYPES_LIST_API_URL = "https://pokeapi.co/api/v2/type";
    private static final String POKEMON_LIST_API_URL = "https://pokeapi.co/api/v2/pokemon?limit=100000&offset=0";

    public FetchTypesAndPokemonFromDatabase(PokemonTypeService pokemonTypeService, PokemonService pokemonService, ObjectMapper objectMapper){
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonService = pokemonService;
        this.objectMapper = objectMapper;
    }
    @Override
    public void run(String... args) throws Exception {
        fetchTypesToDatabase();
        fetchPokemonToDatabase();
        System.out.println("*** Fetch complete ***");
    }

    private void fetchTypesToDatabase() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(POKEMON_TYPES_LIST_API_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode resultsNode = rootNode.path("results");

        for (JsonNode pokemonTypeNode : resultsNode) {
            pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                    pokemonTypeNode.path("name").asText());
        }
    }

    private void fetchPokemonToDatabase() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(POKEMON_LIST_API_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode resultsNode = rootNode.path("results");

        for (JsonNode pokemonNode : resultsNode) {
            pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(
                    extractIdFromUrl(pokemonNode.path("url").asText()));
        }
    }

    private int extractIdFromUrl(String url) {
        url = url.substring(0, url.length() - 1);

        int lastSlashIndex = url.lastIndexOf("/");

        return Integer.parseInt(url.substring(lastSlashIndex + 1));
    }
}
