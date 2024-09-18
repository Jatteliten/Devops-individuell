package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonTestUtils {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public JsonNode getJsonFromFile(String fileName){
        try {
            return objectMapper.readTree(
                    new String(
                            Files.readAllBytes(Paths.get(fileName))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
