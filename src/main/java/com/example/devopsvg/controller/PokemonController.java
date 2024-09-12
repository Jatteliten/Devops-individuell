package com.example.devopsvg.controller;

import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.services.PokemonTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pokemon")
public class PokemonController {
    private final PokemonRepo pokemonRepo;
    private final PokemonTypeService pokemonTypeService;

    public PokemonController(PokemonRepo pokemonRepo, PokemonTypeService pokemonTypeService) {
        this.pokemonRepo = pokemonRepo;
        this.pokemonTypeService = pokemonTypeService;
    }

    @GetMapping("/list")
    public String pokemonList(Model model){
        model.addAttribute("pokemonlist", pokemonRepo.findAll());
        model.addAttribute("types", pokemonTypeService.getAllTypeNamesList());
        return "pokemon-list.html";
    }

    @GetMapping("/list-by-type")
    public String pokemonListByColor(@RequestParam("type") String type, Model model) {
        model.addAttribute("pokemonlist", pokemonRepo.findAllByTypes_Name(type));
        model.addAttribute("types", pokemonTypeService.getAllTypeNamesList());
        return "pokemon-list.html";
    }

    @GetMapping("/list-by-search")
    public String pokemonListBySearch(@RequestParam("searchWord") String input, Model model){
        model.addAttribute("pokemonlist", pokemonRepo.findAllByNameIsContainingIgnoreCase(input));
        model.addAttribute("types", pokemonTypeService.getAllTypeNamesList());
        return "pokemon-list.html";
    }

    @GetMapping("/pokemon-info")
    public String pokemonInformation(@RequestParam("pokemonName") String name, Model model){
        model.addAttribute("pokemon", pokemonRepo.findByName(name));
        return "pokemon-info.html";
    }

}
