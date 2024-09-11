package com.example.devopsvg.repos;

import com.example.devopsvg.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PokemonRepo extends JpaRepository<Pokemon, Long> {
    Pokemon findByName(String name);
    List<Pokemon> findAllByTypes_Name(String typeName);
}
