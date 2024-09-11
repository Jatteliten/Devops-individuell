package com.example.devopsvg.controller;

import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.services.PokemonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pokemon")
public class PokemonController {
    private final PokemonService pokemonService;
    private final PokemonRepo pokemonRepo;

    public PokemonController(PokemonService pokemonService, PokemonRepo pokemonRepo) {
        this.pokemonService = pokemonService;
        this.pokemonRepo = pokemonRepo;
    }

    @RequestMapping("/list")
    public List<Pokemon> pokemonList(){
        return pokemonRepo.findAll();
    }
}
