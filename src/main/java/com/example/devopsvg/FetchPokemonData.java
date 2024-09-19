package com.example.devopsvg;

import com.example.devopsvg.repos.PokemonMoveRepo;
import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.example.devopsvg.services.PokemonMoveService;
import com.example.devopsvg.services.PokemonService;
import com.example.devopsvg.services.PokemonTypeService;
import com.example.devopsvg.utils.JsonExtractor;
import com.example.devopsvg.utils.UrlUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class FetchPokemonData implements CommandLineRunner {
    private final ApplicationContext context;
    private final JsonExtractor jsonExtractor = new JsonExtractor();
    private final PokemonTypeService pokemonTypeService;
    private final PokemonService pokemonService;
    private final PokemonMoveService pokemonMoveService;
    private final PokemonRepo pokemonRepo;
    private final PokemonMoveRepo pokemonMoveRepo;
    private final PokemonTypeRepo pokemonTypeRepo;
    private final UrlUtils urlUtils;

    @Value("${pokemon.types.api.url}")
    private String pokemonTypesApiUrl;

    @Value("${pokemon.moves.api.url}")
    private String pokemonMovesApiUrl;

    @Value("${pokemon.list.api.url}")
    private String pokemonListApiUrl;

    @Value("${pokemon.api.alternate_form_limiter}")
    private int alternateFormLimiter;

    public FetchPokemonData(ApplicationContext context, PokemonTypeService pokemonTypeService, PokemonService pokemonService,
                            PokemonMoveService pokemonMoveService, PokemonRepo pokemonRepo, PokemonMoveRepo pokemonMoveRepo, PokemonTypeRepo pokemonTypeRepo, UrlUtils urlUtils){
        this.context = context;
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonService = pokemonService;
        this.pokemonMoveService = pokemonMoveService;
        this.pokemonRepo = pokemonRepo;
        this.pokemonMoveRepo = pokemonMoveRepo;
        this.pokemonTypeRepo = pokemonTypeRepo;
        this.urlUtils = urlUtils;
    }
    @Override
    public void run(String... args) {
        if(pokemonTypeRepo.count() == 0) {
            fetchTypesAndAddRelationshipsToDatabase(jsonExtractor.fetchJsonFromUrl(pokemonTypesApiUrl).path("results"));
        }
        if(pokemonMoveRepo.count() == 0) {
            fetchMovesToDatabase(jsonExtractor.fetchJsonFromUrl(
                    urlUtils.removeResponseLimit(pokemonMovesApiUrl)).path("results"));
        }
        if(pokemonRepo.count() == 0) {
            fetchPokemonToDatabase(jsonExtractor.fetchJsonFromUrl(
                    urlUtils.removeResponseLimit(pokemonListApiUrl)).path("results"));
        }
        System.out.println("*** Fetch complete ***");
        SpringApplication.exit(context, () -> 0);
    }

    private void fetchTypesAndAddRelationshipsToDatabase(JsonNode resultsNode) {
        for (JsonNode pokemonTypeNode : resultsNode) {
            pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                    pokemonTypeNode.path("name").asText());
        }
        System.out.println("*** All types added ***");

        pokemonTypeService.addTypeRelationshipsIfTheyDoNotAlreadyExist();
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
