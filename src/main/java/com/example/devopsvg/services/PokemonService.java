package com.example.devopsvg.services;

import com.example.devopsvg.dto.pokemonViews.PokemonListDto;
import com.example.devopsvg.dto.pokemonViews.PokemonNextOrPreviousDto;
import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.utils.JsonExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Service
public class PokemonService {
    private final JsonExtractor jsonExtractor;
    private final PokemonRepo pokemonRepo;
    private final PokemonTypeService pokemonTypeService;
    private final PokemonMoveService pokemonMoveService;

    @Value("${pokemon.list.api.url}")
    private String pokemonListApiUrl;

    public PokemonService(JsonExtractor jsonExtractor, PokemonRepo pokemonRepo,
                          PokemonTypeService pokemonTypeService, PokemonMoveService pokemonMoveService) {
        this.jsonExtractor = jsonExtractor;
        this.pokemonRepo = pokemonRepo;
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonMoveService = pokemonMoveService;
    }

    public List<Pokemon> getAllPokemonInPokedexOrder() {
        return pokemonRepo.findAllByOrderByPokedexIdAsc();
    }

    public Pokemon getPokemonByName(String name) {
        return pokemonRepo.findByName(name);
    }

    public List<Pokemon> getAllPokemonWithGivenType(String typeName) {
        return pokemonRepo.findAllByTypes_Name(typeName);
    }

    public Pokemon getPokemonByPokedexId(int id) {
        return pokemonRepo.findByPokedexId(id);
    }

    public List<Pokemon> getAllPokemonWithNameThatContainsString(String input) {
        return pokemonRepo.findAllByNameIsContainingIgnoreCase(input);
    }

    public Long countNumberOfPokemonInDatabase() {
        return pokemonRepo.count();
    }

    public List<PokemonListDto> getPokemonListDtoPageInPokedexOrder(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Pokemon> pokemonPage = pokemonRepo.findAllByOrderByPokedexIdAsc(pageable);

        return pokemonPage.stream()
                .map(this::convertPokemonToPokemonListDto)
                .collect(Collectors.toList());
    }

    public void savePokemonToDatabaseIfItDoesNotAlreadyExist(int pokemonId) {
        JsonNode pokemonData = jsonExtractor.getSpecificEntryDataFromApiById(pokemonListApiUrl, pokemonId);
        Optional<Pokemon> tempPokemon = Optional.ofNullable(
                getPokemonByName(pokemonData.path("name").asText()));
        if (tempPokemon.isEmpty()) {
            savePokemonToDatabaseFromJsonNode(pokemonData);
        }
    }

    private void savePokemonToDatabaseFromJsonNode(JsonNode pokemonData) {
        Pokemon newPokemon = createPokemonFromJsonNode(pokemonData);
        pokemonRepo.save(newPokemon);
    }

    public Pokemon createPokemonFromJsonNode(JsonNode pokemonData) {
        return Pokemon.builder()
                .pokedexId(pokemonData.path("id").asInt())
                .name(capitalizeFirstLetter(pokemonData.path("name").asText()))
                .spriteLink(pokemonData.path("sprites")
                        .path("front_default")
                        .asText())
                .artworkLink(pokemonData.path("sprites")
                        .path("other")
                        .path("official-artwork")
                        .path("front_default")
                        .asText())
                .flavorText(findFlavorTextInSpeciesEntry(pokemonData
                        .path("species")
                        .path("url")
                        .asText()))
                .types(pokemonTypeService.getPokemonTypesListFromPokemonEntryFromApi(pokemonData))
                .moves(pokemonMoveService.getMoveListFromApi(pokemonData))
                .build();
    }

    public String findFlavorTextInSpeciesEntry(String url) {
        JsonNode entries = jsonExtractor.fetchJsonFromUrl(url)
                .path("flavor_text_entries");

        for (JsonNode entry : entries) {
            if (entry.path("language").path("name").asText().equals("en")) {
                return removeLineBreaksAndFormFeedCharactersFromFlavorText(
                        entry.path("flavor_text").asText());
            }
        }

        return "No English flavor text found.";
    }

    public List<PokemonListDto> getAllPokemonByTypeForList(String type) {
        return getAllPokemonWithGivenType(type).stream().map(this::convertPokemonToPokemonListDto).toList();
    }

    public PokemonListDto convertPokemonToPokemonListDto(Pokemon pokemon) {
        return PokemonListDto.builder()
                .name(pokemon.getName())
                .pokedexId(pokemon.getPokedexId())
                .spriteLink(pokemon.getSpriteLink())
                .types(pokemon.getTypes())
                .build();
    }

    public PokemonNextOrPreviousDto convertPokemonToPokemonNextOrPreviousDto(Pokemon pokemon) {
        return PokemonNextOrPreviousDto.builder()
                .name(pokemon.getName())
                .spriteLink(pokemon.getSpriteLink())
                .build();
    }

    public Map<PokemonType, Double> calculateDamageTakenMultipliers(Pokemon pokemon) {
        List<PokemonType> pokemonTypes = pokemon.getTypes();
        Map<PokemonType, Double> combinedMultipliers = new HashMap<>();

        for (PokemonType type : pokemonTypes) {
            addDamageMultipliersForType(type, combinedMultipliers);
        }

        return combinedMultipliers.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != 1.0)
                .sorted(Map.Entry.<PokemonType, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private void addDamageMultipliersForType(PokemonType pokemonType, Map<PokemonType, Double> multipliers) {
        updateDamageMultiplier(multipliers, pokemonType.getDoubleDamageFrom(), 2.0);
        updateDamageMultiplier(multipliers, pokemonType.getHalfDamageFrom(), 0.5);
        updateDamageMultiplier(multipliers, pokemonType.getNoDamageFrom(), 0.0);
    }

    private void updateDamageMultiplier(Map<PokemonType, Double> multipliers, List<PokemonType> types, double value) {
        for (PokemonType type : types) {
            multipliers.merge(type, value, (oldValue, newValue) -> oldValue * newValue);
        }
    }

    public String removeLineBreaksAndFormFeedCharactersFromFlavorText(String text) {
        return text.replace("\n", " ").replace("\u000c", " ");
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    public Pokemon findNextPokemonInPokeDex(Pokemon pokemon) {
        if (pokemon.getPokedexId() != getAllPokemonInPokedexOrder().size()) {
            return getPokemonByPokedexId(pokemon.getPokedexId() + 1);
        }
        return null;
    }

    public Pokemon findPreviousPokemonInPokeDex(Pokemon pokemon) {
        if (pokemon.getPokedexId() != 1) {
            return getPokemonByPokedexId(pokemon.getPokedexId() - 1);
        }
        return null;
    }

    public Pokemon getRandomPokemon() {
        Random rand = new Random();

        return getPokemonByPokedexId(rand.nextInt((int) (countNumberOfPokemonInDatabase() + 1)));
    }

}

