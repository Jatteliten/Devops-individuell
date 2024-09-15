package com.example.devopsvg.services;

import com.example.devopsvg.dto.pokemonViews.PokemonListDto;
import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.util.JsonExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {
    private final JsonExtractor jsonExtractor = new JsonExtractor();
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PokemonRepo pokemonRepo;
    private final PokemonTypeService pokemonTypeService;
    private final PokemonMoveService pokemonMoveService;

    @Value("${pokemon.list.api.url}")
    private String pokemonListApiUrl;

    public PokemonService(RestTemplate restTemplate, ObjectMapper objectMapper,
                          PokemonRepo pokemonRepo, PokemonTypeService pokemonTypeService, PokemonMoveService pokemonMoveService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.pokemonRepo = pokemonRepo;
        this.pokemonTypeService = pokemonTypeService;
        this.pokemonMoveService = pokemonMoveService;
    }

    public void savePokemonToDatabaseIfItDoesNotAlreadyExist(int pokemonId){
        JsonNode pokemonData = getPokemonDataFromApi(pokemonId);
        Optional<Pokemon> tempPokemon = Optional.ofNullable(
                pokemonRepo.findByName(pokemonData.path("name").asText()));
        if(tempPokemon.isEmpty()){
            savePokemonToDatabase(pokemonData);
        }
    }

    private void savePokemonToDatabase(JsonNode pokemonData){
        Pokemon newPokemon = createPokemonFromJson(pokemonData);

        pokemonRepo.save(newPokemon);
        System.out.println(newPokemon.getName() + " saved.");
    }

    public Pokemon createPokemonFromJson(JsonNode pokemonData){
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
                .types(pokemonTypeService.getTypesListFromApi(pokemonData))
                .moves(pokemonMoveService.getMoveListFromApi(pokemonData))
                .build();
    }

    public String findFlavorTextInSpeciesEntry(String url){
        try {
            JsonNode entries = jsonExtractor.fetchJsonFromUrl(url)
                    .path("flavor_text_entries");

            for (JsonNode entry : entries) {
                if (entry.path("language").path("name").asText().equals("en")) {
                    return removeLineBreaksAndFormFeedCharactersFromFlavorText(
                            entry.path("flavor_text").asText());
                }
            }

            return "No English flavor text found.";
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode getPokemonDataFromApi(int pokemonId) {
        String jsonResponse = restTemplate.getForObject(pokemonListApiUrl + pokemonId, String.class);

        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PokemonListDto> getAllPokemonForList(){
        return pokemonRepo.findAll().stream().map(this::convertPokemonToPokemonListDto).toList();
    }

    public List<PokemonListDto> getAllPokemonByTypeForList(String type){
        return pokemonRepo.findAllByTypes_Name(type).stream().map(this::convertPokemonToPokemonListDto).toList();
    }

    public PokemonListDto convertPokemonToPokemonListDto(Pokemon pokemon){
        return PokemonListDto.builder()
                .name(pokemon.getName())
                .pokedexId(pokemon.getPokedexId())
                .spriteLink(pokemon.getSpriteLink())
                .types(pokemon.getTypes())
                .build();
    }

    public String removeLineBreaksAndFormFeedCharactersFromFlavorText(String text){
        return text.replace("\n", " ").replace("\u000c", " ");
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

}

