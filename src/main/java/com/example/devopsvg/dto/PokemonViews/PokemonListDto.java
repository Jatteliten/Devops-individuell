package com.example.devopsvg.dto.PokemonViews;

import com.example.devopsvg.model.PokemonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PokemonListDto {
    String name;
    int pokedexId;
    String spriteLink;
    List<PokemonType> types;
}
