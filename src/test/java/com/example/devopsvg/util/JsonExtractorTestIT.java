package com.example.devopsvg.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonExtractorTestIT {
    private final JsonExtractor jsonExtractor = new JsonExtractor();

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