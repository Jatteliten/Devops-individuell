package com.example.devopsvg.repos;

import com.example.devopsvg.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonRepo extends JpaRepository<Pokemon, Long> {
    Pokemon findByName(String name);
}
