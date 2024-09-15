package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonMove;
import com.example.devopsvg.repos.PokemonMoveRepo;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

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
        return StreamSupport.stream(pokemonData.path("moves").spliterator(), false)
                .map(typeNode -> pokemonMoveRepo.findByName(typeNode
                        .path("move")
                        .path("name")
                        .asText()))
                .sorted(Comparator.comparing(move -> move.getType().getName()))
                .toList();
    }

    public JsonNode getMoveDataFromApi(int pokemonMoveId) {
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
