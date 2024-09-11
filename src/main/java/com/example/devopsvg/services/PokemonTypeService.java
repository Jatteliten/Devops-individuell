package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonTypeService {
    private final PokemonTypeRepo pokemonTypeRepo;

    public PokemonTypeService(PokemonTypeRepo pokemonTypeRepo){
        this.pokemonTypeRepo = pokemonTypeRepo;
    }

    public PokemonType findPokemonTypeByName(String name){
        return pokemonTypeRepo.findByName(name);
    }

    public void saveTypeToDatabaseIfItDoesNotAlreadyExist(String name){
        Optional<PokemonType> tempPokemonType = Optional.ofNullable(
                pokemonTypeRepo.findByName(name));

        if(tempPokemonType.isEmpty()){
            pokemonTypeRepo.save(PokemonType.builder()
                    .name(name)
                    .build());
        }
    }

    public List<PokemonType> getTypesListFromApi(JsonNode pokemonData){
        List<PokemonType> types = new ArrayList<>();
        for (JsonNode typeNode : pokemonData.path("types")) {
            types.add(findPokemonTypeByName(
                    typeNode.path("type").path("name").asText()));
        }

        return types;
    }

    public List<String> getAllTypeNamesList(){
        return pokemonTypeRepo.findAll().stream().map(PokemonType::getName).toList();
    }
}
