package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.example.devopsvg.utils.JsonExtractor;
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
    private final JsonExtractor jsonExtractor;

    @Value("${pokemon.types.api.url}")
    private String pokemonTypesApiUrl;

    public PokemonTypeService(PokemonTypeRepo pokemonTypeRepo, JsonExtractor jsonExtractor){
        this.pokemonTypeRepo = pokemonTypeRepo;
        this.jsonExtractor = jsonExtractor;
    }

    public List<PokemonType> getAllPokemonTypes(){
        return pokemonTypeRepo.findAll();
    }

    public PokemonType getPokemonTypeByName(String name){
        return pokemonTypeRepo.findByName(name);
    }

    public Long countNumberOfTypesInDatabase(){
        return pokemonTypeRepo.count();
    }

    public void saveTypeToDatabaseIfItDoesNotAlreadyExist(String name){
        JsonNode pokemonTypeData = jsonExtractor.fetchJsonFromUrl(pokemonTypesApiUrl + name);
        Optional<PokemonType> tempPokemonType = Optional.ofNullable(
                getPokemonTypeByName(name));

        if(tempPokemonType.isEmpty() && !pokemonTypeData.path("pokemon").isEmpty()){
            pokemonTypeRepo.save(PokemonType.builder()
                    .name(name)
                    .build());
        }
    }

    public List<PokemonType> getPokemonTypesListFromPokemonEntryFromApi(JsonNode pokemonData){
        List<PokemonType> types = new ArrayList<>();
        for (JsonNode typeNode : pokemonData.path("types")) {
            types.add(getPokemonTypeByName(
                    typeNode.path("type").path("name").asText()));
        }

        return types;
    }

    @Transactional
    public void addTypeRelationshipsIfTheyDoNotAlreadyExist(){
        List<PokemonType> types = getAllPokemonTypes();

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
        typeData
                .path("damage_relations")
                .path(damageMultiplier)
                .forEach(multiplierType ->
                    typeList.add(getPokemonTypeByName(multiplierType
                            .path("name")
                            .asText())));
    }

    public List<String> getAllTypeNamesList(){
        return getAllPokemonTypes().stream().map(PokemonType::getName).toList();
    }
}
