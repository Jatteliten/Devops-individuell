package com.example.devopsvg.repos;

import com.example.devopsvg.model.PokemonType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonTypeRepo extends JpaRepository<PokemonType, Long> {
    PokemonType findByName(String name);
}
