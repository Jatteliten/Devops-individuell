package com.example.devopsvg.services;

import com.example.devopsvg.dto.pokemonViews.PokemonListDto;
import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonMoveRepo;
import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.repos.PokemonTypeRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import utils.JsonTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class PokemonServiceIT {
    @Autowired
    PokemonRepo pokemonRepo;
    @Autowired
    PokemonService pokemonService;
    @Autowired
    PokemonTypeRepo pokemonTypeRepo;
    @Autowired
    PokemonTypeService pokemonTypeService;
    @Autowired
    PokemonMoveService pokemonMoveService;
    @Autowired
    PokemonMoveRepo pokemonMoveRepo;
    private final JsonTestUtils jsonTestUtils = new JsonTestUtils();
    private final String TEST_POKEMON_NAME = "Bulbasaur";
    private final int TEST_POKEMON_ID = 1;

    @Value("${filepath.types.json}")
    private String typesFilePath;

    @Value("${filepath.bulbasaur.json}")
    private String bulbasaurFilePath;

    @BeforeEach
    void setUp() {
        pokemonRepo.deleteAll();
        pokemonTypeRepo.deleteAll();
        pokemonMoveRepo.deleteAll();
        addTypes();
        addBulbasaurMoves();
    }

    private void addTypes(){
        jsonTestUtils.getJsonFromFile(typesFilePath)
                .forEach(typeData ->
                        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                                typeData.path("name").asText()));
    }

    private void addBulbasaurMoves(){
        jsonTestUtils.getJsonFromFile(bulbasaurFilePath)
                .path("moves")
                .forEach(typeData ->
                        pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist(
                                typeData.path("move").path("name").asText()));

    }

    @Test
    @Transactional
    void savePokemonToDatabaseShouldSavePokemon() {
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(1);

        Assertions.assertEquals(1, pokemonService.getAllPokemonInPokedexOrder().size());
    }

    @Test
    @Transactional
    void savePokemonToDataBaseShouldSaveCorrectPokemon(){
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        Assertions.assertEquals(1, pokemonService.getPokemonByName(TEST_POKEMON_NAME).getPokedexId());
        Assertions.assertEquals(TEST_POKEMON_NAME, pokemonService.getPokemonByName(TEST_POKEMON_NAME).getName());
    }

    @Test
    @Transactional
    void savePokemonToDataBaseShouldSaveCorrectTypes(){
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("grass");
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("poison");
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        Assertions.assertEquals(2, pokemonService.getPokemonByName(TEST_POKEMON_NAME).getTypes().size());
        Assertions.assertEquals("grass", pokemonService.getPokemonByName(TEST_POKEMON_NAME).getTypes().get(0).getName());
        Assertions.assertEquals("poison", pokemonService.getPokemonByName(TEST_POKEMON_NAME).getTypes().get(1).getName());
    }

    @Test
    @Transactional
    void savePokemonToDataBaseShouldSaveCorrectMoves(){
        String tackleName = "tackle";
        pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist(tackleName);
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        Assertions.assertTrue(pokemonService.getPokemonByName(TEST_POKEMON_NAME).getMoves()
                .contains(pokemonMoveService.getPokemonMoveByName(tackleName)));
    }

    @Test
    @Transactional
    void savePokemonToDataBaseShouldSaveCorrectFlavorText(){
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        String expectedFlavorText = "A strange seed was planted on its back at birth. " +
                "The plant sprouts and grows with this POKéMON.";

        Assertions.assertEquals(expectedFlavorText, pokemonService.getPokemonByName(TEST_POKEMON_NAME).getFlavorText());
    }


    @Test
    @Transactional
    void findFlavorTextInSpeciesEntryShouldFindCorrectFlavorText() {
        String bulbasaurFlavorText = "A strange seed was planted on its back at birth. The plant sprouts and grows with this POKéMON.";
        String bulbasaurSpeciesUrl = "https://pokeapi.co/api/v2/pokemon-species/1/";

        Assertions.assertEquals(pokemonService.findFlavorTextInSpeciesEntry(bulbasaurSpeciesUrl), bulbasaurFlavorText);
    }

    @Test
    @Transactional
    void getAllPokemonInPokedexOrderShouldGetAllPokemonInCorrectOrderByPokedexId(){
        pokemonRepo.save(Pokemon.builder().pokedexId(3).build());
        pokemonRepo.save(Pokemon.builder().pokedexId(1).build());
        pokemonRepo.save(Pokemon.builder().pokedexId(4).build());
        pokemonRepo.save(Pokemon.builder().pokedexId(2).build());

        List<Pokemon> pokemonList = pokemonService.getAllPokemonInPokedexOrder();

        for(int i = 0; i < pokemonList.size(); i++){
            Assertions.assertEquals(i + 1, pokemonList.get(i).getPokedexId());
        }
    }

    @Test
    @Transactional
    void getPokemonListDtoPageInPokedexOrderShouldReturnCorrectPage(){
        pokemonRepo.save(Pokemon.builder().pokedexId(1).build());
        pokemonRepo.save(Pokemon.builder().pokedexId(2).build());
        pokemonRepo.save(Pokemon.builder().pokedexId(3).build());
        pokemonRepo.save(Pokemon.builder().pokedexId(4).build());
        pokemonRepo.save(Pokemon.builder().pokedexId(5).build());

        List<PokemonListDto> firstPageList = pokemonService.getPokemonListDtoPageInPokedexOrder(0, 3);
        List<PokemonListDto> secondPageList = pokemonService.getPokemonListDtoPageInPokedexOrder(1, 3);

        Assertions.assertEquals(3, firstPageList.size());
        Assertions.assertEquals(2, secondPageList.size());

        for(int i = 0; i < firstPageList.size(); i++){
            Assertions.assertEquals(firstPageList.get(i).getPokedexId(), i +1);
        }
        for(int i = 0; i < secondPageList.size(); i++){
            Assertions.assertEquals(secondPageList.get(i).getPokedexId(), i + 4);
        }
    }


    @Test
    @Transactional
    void calculateDamageMultipliersShouldAddCorrectMultipliers(){
        double damageFromFire = 2.0;
        double damageFromGrass = 0.25;

        jsonTestUtils.getJsonFromFile(typesFilePath)
                    .forEach(typeData ->
                            pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist(
                                    typeData.path("name").asText()));
        pokemonTypeService.getAllPokemonTypes().forEach(type ->{
            type.setHalfDamageFrom(new ArrayList<>());
            type.setDoubleDamageFrom(new ArrayList<>());
            type.setNoDamageFrom(new ArrayList<>());
            pokemonTypeRepo.save(type);
        });

        pokemonTypeService.addTypeRelationshipsIfTheyDoNotAlreadyExist();
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        Pokemon pokemon = pokemonService.getPokemonByName(TEST_POKEMON_NAME);
        Map<PokemonType, Double> damageModifiers = pokemonService.calculateDamageTakenMultipliers(pokemon);

        Assertions.assertEquals(damageFromFire, damageModifiers.get(pokemonTypeService.getPokemonTypeByName("fire")));
        Assertions.assertEquals(damageFromGrass, damageModifiers.get(pokemonTypeService.getPokemonTypeByName("grass")));
    }

}
