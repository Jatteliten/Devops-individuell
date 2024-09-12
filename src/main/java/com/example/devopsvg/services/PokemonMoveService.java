package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonMove;
import com.example.devopsvg.repos.PokemonMoveRepo;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonMoveService {
    private static final String POKE_API_URL = "https://pokeapi.co/api/v2/move/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PokemonTypeRepo pokemonTypeRepo;
    private final PokemonMoveRepo pokemonMoveRepo;

    public PokemonMoveService(RestTemplate restTemplate, ObjectMapper objectMapper,
                              PokemonTypeRepo pokemonTypeRepo, PokemonMoveRepo pokemonMoveRepo) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.pokemonTypeRepo = pokemonTypeRepo;
        this.pokemonMoveRepo = pokemonMoveRepo;
    }

    public void saveMoveToDatabaseIfItDoesNotAlreadyExist(int pokemonId){
        JsonNode pokemonData = getPokemonDMoveDataFromApi(pokemonId);
        Optional<PokemonMove> tempPokemonMove = Optional.ofNullable(
                pokemonMoveRepo.findByName(pokemonData.path("name").asText()));
        if(tempPokemonMove.isEmpty()){
            savePokemonMoveToDatabase(pokemonData);
        }
    }

    private void savePokemonMoveToDatabase(JsonNode pokemonMoveData){
        PokemonMove newPokemonMove = createPokemonMoveFromJson(pokemonMoveData);

        pokemonMoveRepo.save(newPokemonMove);
    }

    public PokemonMove createPokemonMoveFromJson(JsonNode pokemonMoveJson) {
        return PokemonMove.builder()
                .name(pokemonMoveJson.path("name").asText())
                .damageClass(pokemonMoveJson.path("damage_class")
                        .path("name")
                        .asText())
                .type(pokemonTypeRepo.findByName(pokemonMoveJson.path("type")
                        .path("name")
                        .asText()))
                .build();
    }

    public List<PokemonMove> getMoveListFromApi(JsonNode pokemonData){
        List<PokemonMove> moves = new ArrayList<>();
        for (JsonNode typeNode : pokemonData.path("moves")) {
            moves.add(pokemonMoveRepo.findByName(
                    typeNode.path("move").path("name").asText()));
        }

        return moves;
    }

    public JsonNode getPokemonDMoveDataFromApi(int pokemonMoveId) {
        String url = POKE_API_URL + pokemonMoveId;
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
