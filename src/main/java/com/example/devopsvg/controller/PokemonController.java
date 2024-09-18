package com.example.devopsvg.controller;

import com.example.devopsvg.dto.pokemonViews.PokemonNextOrPreviousDto;
import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.services.PokemonService;
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
    private final PokemonService pokemonService;

    public PokemonController(PokemonRepo pokemonRepo, PokemonTypeService pokemonTypeService,
                             PokemonService pokemonService) {
        this.pokemonRepo = pokemonRepo;
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonService = pokemonService;
    }

    @GetMapping("/list")
    public String pokemonList(Model model){
        model.addAttribute("pokemonlist", pokemonService.getAllPokemonForList());
        model.addAttribute("types", pokemonTypeService.getAllTypeNamesList());
        return "pokemon-list.html";
    }

    @GetMapping("/list-by-type")
    public String pokemonListByColor(@RequestParam("type") String type, Model model) {
        model.addAttribute("pokemonlist", pokemonService.getAllPokemonByTypeForList(type));
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
        Pokemon pokemon = pokemonRepo.findByName(pokemonService.capitalizeFirstLetter(name));

        model.addAttribute("pokemon", pokemon);
        model.addAttribute("pokemonTypeMatchUps", pokemonService.calculateDamageTakenMultipliers(pokemon));
        findNextOrPreviousPokemonAndAddToModelIfItIsNotNull(
                pokemonService.findPreviousPokemonInPokeDex(pokemon), model, "previousPokemon");
        findNextOrPreviousPokemonAndAddToModelIfItIsNotNull(
                pokemonService.findNextPokemonInPokeDex(pokemon), model, "nextPokemon");

        return "pokemon-info.html";
    }

    private void findNextOrPreviousPokemonAndAddToModelIfItIsNotNull(Pokemon pokemon, Model model,
                                                                     String modelAttribute) {
        if (pokemon != null) {
            PokemonNextOrPreviousDto pokemonNextOrPrevious =
                    pokemonService.convertPokemonToPokemonNextOrPreviousDto(pokemon);
            model.addAttribute(modelAttribute, pokemonNextOrPrevious);
        } else {
            model.addAttribute(modelAttribute, "empty");
        }
    }

}
