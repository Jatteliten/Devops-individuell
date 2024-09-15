package com.example.devopsvg;

import com.example.devopsvg.services.PokemonMoveService;
import com.example.devopsvg.services.PokemonService;
import com.example.devopsvg.services.PokemonTypeService;
import com.example.devopsvg.util.JsonExtractor;
import com.example.devopsvg.util.UrlUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class FetchPokemonData implements CommandLineRunner {
    private final JsonExtractor jsonExtractor = new JsonExtractor();
    private final PokemonTypeService pokemonTypeService;
    private final PokemonService pokemonService;
    private final PokemonMoveService pokemonMoveService;
    private final UrlUtils urlUtils;

    @Value("${pokemon.types.api.url}")
    private String pokemonTypesApiUrl;

    @Value("${pokemon.moves.api.url}")
    private String pokemonMovesApiUrl;

    @Value("${pokemon.list.api.url}")
    private String pokemonListApiUrl;

    @Value("${pokemon.api.alternate_form_limiter}")
    private int alternateFormLimiter;

    public FetchPokemonData(PokemonTypeService pokemonTypeService, PokemonService pokemonService,
                            PokemonMoveService pokemonMoveService, UrlUtils urlUtils){
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonService = pokemonService;
        this.pokemonMoveService = pokemonMoveService;
        this.urlUtils = urlUtils;
    }
    @Override
    public void run(String... args) {
        fetchTypesAndAddRelationshipsToDatabase(jsonExtractor.fetchJsonFromUrl(pokemonTypesApiUrl).path("results"));
        fetchMovesToDatabase(jsonExtractor.fetchJsonFromUrl(
                urlUtils.removeResponseLimit(pokemonMovesApiUrl)).path("results"));
        fetchPokemonToDatabase(jsonExtractor.fetchJsonFromUrl(
                urlUtils.removeResponseLimit(pokemonListApiUrl)).path("results"));
        System.out.println("*** Fetch complete ***");
    }

    private void fetchTypesAndAddRelationshipsToDatabase(JsonNode resultsNode) {
        for (JsonNode pokemonTypeNode : resultsNode) {
            pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                    pokemonTypeNode.path("name").asText());
        }
        System.out.println("*** All types added ***");
        pokemonTypeService.addTypeRelationships();
        System.out.println("*** Type relationships added ***");
    }

    private void fetchPokemonToDatabase(JsonNode resultsNode) {
        for (JsonNode pokemonNode : resultsNode) {
            if(urlUtils.extractIdFromUrl(pokemonNode.path("url").asText()) < alternateFormLimiter) {
                pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(
                        urlUtils.extractIdFromUrl(pokemonNode.path("url").asText()));
            }
        }
        System.out.println("*** All Pokémon added ***");
    }

    private void fetchMovesToDatabase(JsonNode resultsNode) {
        for (JsonNode pokemonMoveNode : resultsNode) {
            pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist(
                    urlUtils.extractIdFromUrl(pokemonMoveNode.path("url").asText()));
        }
        System.out.println("*** All moves added ***");
    }

}
