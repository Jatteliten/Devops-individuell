package com.example.devopsvg.repos;

import com.example.devopsvg.model.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PokemonRepo extends JpaRepository<Pokemon, Long> {
    Pokemon findByName(String name);
    Pokemon findByPokedexId(int id);
    List<Pokemon> findAllByTypes_Name(String typeName);
    List<Pokemon> findAllByNameIsContainingIgnoreCase(String input);
    List<Pokemon> findAllByOrderByPokedexIdAsc();
    Page<Pokemon> findAllByOrderByPokedexIdAsc(Pageable pageable);
}
