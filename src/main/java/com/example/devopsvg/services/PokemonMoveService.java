package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonMove;
import com.example.devopsvg.repos.PokemonMoveRepo;
import com.example.devopsvg.utils.JsonExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonMoveService {
    private final PokemonTypeService pokemonTypeService;
    private final PokemonMoveRepo pokemonMoveRepo;
    private final JsonExtractor jsonExtractor;

    @Value("${pokemon.moves.api.url}")
    private String pokemonMovesApiUrl;

    public PokemonMoveService(PokemonTypeService pokemonTypeService, PokemonMoveRepo pokemonMoveRepo,
                              JsonExtractor jsonExtractor) {
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonMoveRepo = pokemonMoveRepo;
        this.jsonExtractor = jsonExtractor;
    }

    public PokemonMove getPokemonMoveByName(String name){
        return pokemonMoveRepo.findByName(name);
    }

    public Long countNumberOfMovesInDatabase(){ return pokemonMoveRepo.count(); }

    public void saveMoveToDatabaseIfItDoesNotAlreadyExist(int pokemonMoveId){
        JsonNode pokemonData = jsonExtractor.fetchJsonFromUrl(pokemonMovesApiUrl + pokemonMoveId);
        Optional<PokemonMove> tempPokemonMove = Optional.ofNullable(
                getPokemonMoveByName(pokemonData.path("name").asText()));
        if(tempPokemonMove.isEmpty()){
            saveMoveToDatabase(pokemonData);
        }
    }

    private void saveMoveToDatabase(JsonNode pokemonMoveData){
        PokemonMove newPokemonMove = createMoveFromJson(pokemonMoveData);

        pokemonMoveRepo.save(newPokemonMove);
    }

    public PokemonMove createMoveFromJson(JsonNode pokemonMoveData) {
        return PokemonMove.builder()
                .name(pokemonMoveData
                        .path("name")
                        .asText())
                .damageClass(pokemonMoveData.path("damage_class")
                        .path("name")
                        .asText())
                .type(pokemonTypeService.getPokemonTypeByName(pokemonMoveData.path("type")
                        .path("name")
                        .asText()))
                .build();
    }

    public List<PokemonMove> getMoveListFromApi(JsonNode pokemonData){
        List<PokemonMove> moves = new ArrayList<>();
        for(JsonNode move: (pokemonData.path("moves"))){
            moves.add(getPokemonMoveByName(move
                    .path("move")
                    .path("name")
                    .asText()));
        }
        return moves;
    }

}
