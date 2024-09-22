package com.example.devopsvg;

import com.example.devopsvg.services.PokemonMoveService;
import com.example.devopsvg.services.PokemonService;
import com.example.devopsvg.services.PokemonTypeService;
import com.example.devopsvg.utils.JsonExtractor;
import com.example.devopsvg.utils.UrlUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class FetchPokemonData implements CommandLineRunner {
    private final JsonExtractor jsonExtractor;
    private final PokemonTypeService pokemonTypeService;
    private final PokemonService pokemonService;
    private final PokemonMoveService pokemonMoveService;
    private final UrlUtils urlUtils;
    private int currentProgress;
    private int numberOfOperations;

    @Value("${pokemon.types.api.url}")
    private String pokemonTypesApiUrl;

    @Value("${pokemon.moves.api.url}")
    private String pokemonMovesApiUrl;

    @Value("${pokemon.list.api.url}")
    private String pokemonListApiUrl;

    @Value("${pokemon.api.alternate_form_limiter}")
    private int alternateFormLimiter;

    public FetchPokemonData(JsonExtractor jsonExtractor, PokemonTypeService pokemonTypeService, PokemonService pokemonService,
                            PokemonMoveService pokemonMoveService, UrlUtils urlUtils){
        this.jsonExtractor = jsonExtractor;
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonService = pokemonService;
        this.pokemonMoveService = pokemonMoveService;
        this.urlUtils = urlUtils;
    }

    @Override
    public void run(String... args) {
        addTypesToDatabaseIfTheyAreNotAlreadyPresent();
        addMovesToDatabaseIfTheyAreaNotAlreadyPresent();
        addPokemonToDatabaseIfTheyAreNotAlreadyPresent();

        System.out.println("\n*** Database populated. Starting application ***");
    }

    private void addTypesToDatabaseIfTheyAreNotAlreadyPresent() {
        if(pokemonTypeService.countNumberOfTypesInDatabase() == 0){
            fetchTypesAndAddRelationshipsToDatabase(
                    jsonExtractor.fetchJsonFromUrl(pokemonTypesApiUrl).path("results"));
        }else{
            System.out.println("*** Types already present in database ***");
        }
    }

    private void addMovesToDatabaseIfTheyAreaNotAlreadyPresent() {
        if(pokemonMoveService.countNumberOfMovesInDatabase() == 0){
            fetchMovesToDatabase(jsonExtractor.fetchJsonFromUrl(
                    urlUtils.removeResponseLimit(pokemonMovesApiUrl)).path("results"));
        }else{
            System.out.println("*** Moves already present in database ***");
        }
    }

    private void addPokemonToDatabaseIfTheyAreNotAlreadyPresent() {
        if(pokemonService.countNumberOfPokemonInDatabase() == 0){
            fetchPokemonToDatabase(jsonExtractor.fetchJsonFromUrl(
                    urlUtils.removeResponseLimit(pokemonListApiUrl)).path("results"));
        }else{
            System.out.println("*** Pokemon already present in database **");
        }
    }

    private void fetchTypesAndAddRelationshipsToDatabase(JsonNode resultsNode) {
        resetProgress(resultsNode, "Types");

        for (JsonNode pokemonTypeNode : resultsNode) {
            pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                    pokemonTypeNode.path("name").asText());

            incrementProgressAndPrintProgressBar(numberOfOperations);
        }
        pokemonTypeService.addTypeRelationshipsIfTheyDoNotAlreadyExist();
    }

    private void fetchMovesToDatabase(JsonNode resultsNode) {
        resetProgress(resultsNode, "Moves");

        for (JsonNode pokemonMoveNode : resultsNode) {
            pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist(
                    urlUtils.extractIdFromUrl(pokemonMoveNode.path("url").asText()));

            incrementProgressAndPrintProgressBar(numberOfOperations);
        }
    }

    private void fetchPokemonToDatabase(JsonNode resultsNode) {
        resetProgress(resultsNode, "Pokemon");

        for (JsonNode pokemonNode : resultsNode) {
            if (urlUtils.extractIdFromUrl(pokemonNode.path("url").asText()) < alternateFormLimiter) {
                pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(
                        urlUtils.extractIdFromUrl(pokemonNode.path("url").asText()));
            }
            incrementProgressAndPrintProgressBar(numberOfOperations);
        }
    }

    private void resetProgress(JsonNode resultsNode, String entityToAddToDatabase){
        System.out.println("\n--- Adding " + entityToAddToDatabase + " to database ---");
        currentProgress = 0;
        numberOfOperations = resultsNode.size();
    }

    private void incrementProgressAndPrintProgressBar(int totalMoves) {
        currentProgress++;
        int progressBarWidth = 30;
        int progress = (int) ((double) currentProgress / totalMoves * progressBarWidth);

        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < progressBarWidth; i++) {
            if (i < progress) {
                progressBar.append("=");
            } else {
                progressBar.append(" ");
            }
        }
        progressBar.append("] ");

        System.out.print("\r" + progressBar + (currentProgress * 100 / totalMoves) + "%");

        System.out.flush();
    }

}
