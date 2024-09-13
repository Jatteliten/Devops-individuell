package com.example.devopsvg.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
public class PokemonMove {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String damageClass;

    @ManyToMany
    private List<Pokemon> pokemon;

    @ManyToOne
    private PokemonType type;
}
