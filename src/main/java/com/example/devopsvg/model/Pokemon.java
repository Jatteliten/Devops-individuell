package com.example.devopsvg.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class Pokemon {
    @Id
    @GeneratedValue
    private Long id;

    private int pokedexId;
    private String name;
    private String flavorText;
    private String spriteLink;
    private String artworkLink;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<PokemonType> types;
    @ManyToMany
    private List<PokemonMove> moves;

}
