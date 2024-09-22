package com.example.devopsvg;

import com.example.devopsvg.controller.PokemonController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DevopsVgApplicationTests {

    @Autowired
    private PokemonController pokemonController;
    @Test
    void contextLoads() {
        assertThat(pokemonController).isNotNull();
    }

}
