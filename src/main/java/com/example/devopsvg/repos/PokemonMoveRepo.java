package com.example.devopsvg.repos;

import com.example.devopsvg.model.PokemonMove;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonMoveRepo extends JpaRepository<PokemonMove, Long> {
    PokemonMove findByName(String name);
}
