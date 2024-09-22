package com.example.devopsvg.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JsonExtractorTestIT {
    @Autowired
    private JsonExtractor jsonExtractor;

    @Test
    void fetchJsonFromUrlShouldFetchCorrectData() {
        Assertions.assertTrue(jsonExtractor.fetchJsonFromUrl("https://pokeapi.co/api/v2/type/")
                .has("results"));
        Assertions.assertEquals("normal", jsonExtractor.fetchJsonFromUrl("https://pokeapi.co/api/v2/type/")
                .path("results")
                .get(0)
                .path("name")
                .asText());
    }

}