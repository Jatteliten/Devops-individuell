package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.example.devopsvg.util.JsonExtractor;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonTypeService {
    private final PokemonTypeRepo pokemonTypeRepo;

    private final JsonExtractor jsonExtractor = new JsonExtractor();

    @Value("${pokemon.types.api.url}")
    private String pokemonTypesApiUrl;

    public PokemonTypeService(PokemonTypeRepo pokemonTypeRepo){
        this.pokemonTypeRepo = pokemonTypeRepo;
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
            types.add(pokemonTypeRepo.findByName(
                    typeNode.path("type").path("name").asText()));
        }

        return types;
    }

    @Transactional
    public void addTypeRelationships(){
        List<PokemonType> types = pokemonTypeRepo.findAll();

        for(PokemonType type: types) {
            if (type.getHalfDamageFrom().isEmpty() && type.getDoubleDamageFrom().isEmpty()) {
                JsonNode typeData = jsonExtractor.fetchJsonFromUrl(pokemonTypesApiUrl + type.getName());
                addDamageModifier(typeData, "double_damage_from", type.getDoubleDamageFrom());
                addDamageModifier(typeData, "half_damage_from", type.getHalfDamageFrom());
                addDamageModifier(typeData, "no_damage_from", type.getNoDamageFrom());
                pokemonTypeRepo.save(type);
            }
        }
    }

    private void addDamageModifier(JsonNode typeData, String damageMultiplier, List<PokemonType> typeList){
        typeData.path("damage_relations").path(damageMultiplier).forEach(multiplierType ->
                typeList.add(pokemonTypeRepo.findByName(multiplierType.path("name").asText())));
    }

    public List<String> getAllTypeNamesList(){
        return pokemonTypeRepo.findAll().stream().map(PokemonType::getName).toList();
    }
}
