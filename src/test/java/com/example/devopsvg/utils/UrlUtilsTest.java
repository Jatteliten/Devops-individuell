package com.example.devopsvg.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = "pokemon.api.remove_response_limit=?limit=10000&offset=0")
class UrlUtilsTest {
    @Autowired
    private UrlUtils urlUtils;

    @Test
    void extractIdFromUrlShouldExtractId() {
        String url = "example.com/2/";
        int expectedId = 2;

        Assertions.assertEquals(expectedId, urlUtils.extractIdFromUrl(url));
    }

    @Test
    void removeResponseLimitShouldAddCorrectTextToUrl() {
        String url = "example.com/pokemon/";
        String expectedUrl = "example.com/pokemon?limit=10000&offset=0";

        Assertions.assertEquals(expectedUrl, urlUtils.removeResponseLimit(url));
    }
}