package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonMove;
import com.example.devopsvg.repos.PokemonMoveRepo;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonMoveService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PokemonTypeRepo pokemonTypeRepo;
    private final PokemonMoveRepo pokemonMoveRepo;
    @Value("${pokemon.moves.api.url}")
    private String pokemonMovesApiUrl;

    public PokemonMoveService(RestTemplate restTemplate, ObjectMapper objectMapper,
                              PokemonTypeRepo pokemonTypeRepo, PokemonMoveRepo pokemonMoveRepo) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.pokemonTypeRepo = pokemonTypeRepo;
        this.pokemonMoveRepo = pokemonMoveRepo;
    }

    public void saveMoveToDatabaseIfItDoesNotAlreadyExist(int pokemonMoveId){
        JsonNode pokemonData = getMoveDataFromApi(pokemonMoveId);
        Optional<PokemonMove> tempPokemonMove = Optional.ofNullable(
                pokemonMoveRepo.findByName(pokemonData.path("name").asText()));
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
                .type(pokemonTypeRepo.findByName(pokemonMoveData.path("type")
                        .path("name")
                        .asText()))
                .build();
    }

    public List<PokemonMove> getMoveListFromApi(JsonNode pokemonData){
        List<PokemonMove> moves = new ArrayList<>();
        for(JsonNode move: (pokemonData.path("moves"))){
            moves.add(pokemonMoveRepo.findByName(move
                    .path("move")
                    .path("name")
                    .asText()));
        }
        return moves;
    }

    public JsonNode getMoveDataFromApi(int pokemonMoveId) {
        String url = pokemonMovesApiUrl + pokemonMoveId;
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
