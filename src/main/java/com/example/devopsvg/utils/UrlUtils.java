package com.example.devopsvg.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlUtils {

    @Value("${pokemon.api.remove_response_limit}")
    private String removeResponseLimit;

    public int extractIdFromUrl(String url) {
        url = url.substring(0, url.length() - 1);

        int lastSlashIndex = url.lastIndexOf("/");

        return Integer.parseInt(url.substring(lastSlashIndex + 1));
    }

    public String removeResponseLimit(String url){
        return url.substring(0, url.length() -1) + removeResponseLimit;
    }
}
