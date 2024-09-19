package com.example.devopsvg.services;

import com.example.devopsvg.model.Pokemon;
import com.example.devopsvg.model.PokemonType;
import com.example.devopsvg.repos.PokemonMoveRepo;
import com.example.devopsvg.repos.PokemonRepo;
import com.example.devopsvg.repos.PokemonTypeRepo;
import com.example.devopsvg.utils.UrlUtils;
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
    private final UrlUtils urlUtils = new UrlUtils();
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
                        pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist(urlUtils.extractIdFromUrl(
                                typeData.path("move").path("url").asText())));

    }

    @Test
    @Transactional
    void savePokemonToDatabaseShouldSavePokemon() {
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(1);

        Assertions.assertEquals(1, pokemonRepo.findAll().size());
    }

    @Test
    @Transactional
    void savePokemonToDataBaseShouldSaveCorrectPokemon(){
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        Assertions.assertEquals(1, pokemonRepo.findByName(TEST_POKEMON_NAME).getPokedexId());
        Assertions.assertEquals(TEST_POKEMON_NAME, pokemonRepo.findByName(TEST_POKEMON_NAME).getName());
    }

    @Test
    @Transactional
    void savePokemonToDataBaseShouldSaveCorrectTypes(){
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("grass");
        pokemonTypeService.saveTypeToDatabaseIfItDoesNotAlreadyExist("poison");
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        Assertions.assertEquals(2, pokemonRepo.findByName(TEST_POKEMON_NAME).getTypes().size());
        Assertions.assertEquals("grass", pokemonRepo.findByName(TEST_POKEMON_NAME).getTypes().get(0).getName());
        Assertions.assertEquals("poison", pokemonRepo.findByName(TEST_POKEMON_NAME).getTypes().get(1).getName());
    }

    @Test
    @Transactional
    void savePokemonToDataBaseShouldSaveCorrectMoves(){
        int tackleMoveId = 33;
        String tackleName = "tackle";
        pokemonMoveService.saveMoveToDatabaseIfItDoesNotAlreadyExist(tackleMoveId);
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        Assertions.assertTrue(pokemonRepo.findByName(TEST_POKEMON_NAME).getMoves()
                .contains(pokemonMoveRepo.findByName(tackleName)));
    }

    @Test
    @Transactional
    void savePokemonToDataBaseShouldSaveCorrectFlavorText(){
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        String expectedFlavorText = "A strange seed was planted on its back at birth. " +
                "The plant sprouts and grows with this POKéMON.";
        String actualFlavorText = pokemonRepo.findByName(TEST_POKEMON_NAME).getFlavorText();

        Assertions.assertEquals(expectedFlavorText, actualFlavorText);
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
        pokemonTypeRepo.findAll().forEach(type ->{
            type.setHalfDamageFrom(new ArrayList<>());
            type.setDoubleDamageFrom(new ArrayList<>());
            type.setNoDamageFrom(new ArrayList<>());
            pokemonTypeRepo.save(type);
        });

        pokemonTypeService.addTypeRelationshipsIfTheyDoNotAlreadyExist();
        pokemonService.savePokemonToDatabaseIfItDoesNotAlreadyExist(TEST_POKEMON_ID);

        Pokemon pokemon = pokemonRepo.findByName(TEST_POKEMON_NAME);
        Map<PokemonType, Double> damageModifiers = pokemonService.calculateDamageTakenMultipliers(pokemon);

        Assertions.assertEquals(damageFromFire, damageModifiers.get(pokemonTypeRepo.findByName("fire")));
        Assertions.assertEquals(damageFromGrass, damageModifiers.get(pokemonTypeRepo.findByName("grass")));
    }

}
