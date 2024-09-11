package com.example.devopsvg.services;

import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonTypeRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PokemonTypeService {
    PokemonTypeRepo pokemonTypeRepo;

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
}
