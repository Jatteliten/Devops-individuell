package com.example.devopsvg;

import com.example.devopsvg.services.PokemonMoveService;
import com.example.devopsvg.services.PokemonService;
import com.example.devopsvg.services.PokemonTypeService;
import com.example.devopsvg.util.JsonExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class FetchData implements CommandLineRunner {
    private final JsonExtractor jsonExtractor = new JsonExtractor();
    private final PokemonTypeService pokemonTypeService;
    private final PokemonService pokemonService;
    private final PokemonMoveService pokemonMoveService;

    @Value("${pokemon.types.api.url}")
    private String pokemonTypesApiUrl;

    @Value("${pokemon.moves.api.url}")
    private String pokemonMovesApiUrl;

    @Value("${pokemon.list.api.url}")
    private String pokemonListApiUrl;

    @Value("${pokemon.api.alternate_form_limiter}")
    private int alternateFormLimiter;

    @Value("${pokemon.api.remove_response_limit}")
    private String removeResponseLimit;

    public FetchData(PokemonTypeService pokemonTypeService, PokemonService pokemonService,
                     PokemonMoveService pokemonMoveService){
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonService = pokemonService;
        this.pokemonMoveService = pokemonMoveService;
    }
    @Override
    public void run(String... args) throws Exception {
        fetchTypesToDatabase(jsonExtractor.fetchJsonFromUrl(pokemonTypesApiUrl).path("results"));
        fetchMovesToDatabase(jsonExtractor.fetchJsonFromUrl(
                removeResponseLimit(pokemonMovesApiUrl)).path("results"));
        fetchPokemonToDatabase(jsonExtractor.fetchJsonFromUrl(
                removeResponseLimit(pokemonListApiUrl)).path("results"));
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
            if(extractIdFromUrl(pokemonNode.path("url").asText()) < alternateFormLimiter) {
                pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(
                        extractIdFromUrl(pokemonNode.path("url").asText()));
            }
        }
        System.out.println("*** All Pokémon added ***");
    }

    private void fetchMovesToDatabase(JsonNode resultsNode) {
        for (JsonNode pokemonMoveNode : resultsNode) {
            pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist(
                    extractIdFromUrl(pokemonMoveNode.path("url").asText()));
        }
        System.out.println("*** All moves added ***");
    }

    private int extractIdFromUrl(String url) {
        url = url.substring(0, url.length() - 1);

        int lastSlashIndex = url.lastIndexOf("/");

        return Integer.parseInt(url.substring(lastSlashIndex + 1));
    }

    private String removeResponseLimit(String url){
        return url.substring(0, url.length() -1) + removeResponseLimit;
    }
}
