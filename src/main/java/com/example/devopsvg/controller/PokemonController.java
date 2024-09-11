package com.example.devopsvg.controller;

import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.services.PokemonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pokemon")
public class PokemonController {
    private final PokemonService pokemonService;
    private final PokemonRepo pokemonRepo;

    public PokemonController(PokemonService pokemonService, PokemonRepo pokemonRepo) {
        this.pokemonService = pokemonService;
        this.pokemonRepo = pokemonRepo;
    }

    @GetMapping("/list")
    public String pokemonList(Model model){
        model.addAttribute("pokemonlist", pokemonRepo.findAll());
        return "pokemon.html";
    }

    @GetMapping("/list-by-type")
    public String pokemonListByColor(@RequestParam("type") String type, Model model) {
        model.addAttribute("pokemonlist", pokemonRepo.findAllByTypes_Name(type));
        return "pokemon.html";
    }

}
