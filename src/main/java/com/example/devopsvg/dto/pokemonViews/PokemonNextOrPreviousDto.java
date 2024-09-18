package com.example.devopsvg.dto.pokemonViews;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PokemonNextOrPreviousDto {
    String name;
    String spriteLink;
}
