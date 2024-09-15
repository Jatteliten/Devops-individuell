package com.example.devopsvg.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PokemonType {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    private List<Pokemon> pokemon;

    @ManyToMany
    private List<PokemonType> doubleDamageFrom;

    @ManyToMany
    private List<PokemonType> halfDamageFrom;

    @ManyToMany
    private List<PokemonType> noDamageFrom;
}
