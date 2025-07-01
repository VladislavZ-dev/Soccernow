package pt.ul.fc.css.javafx.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import pt.ul.fc.css.javafx.dto.PlaceDTO;

public class PlaceApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/places";
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<PlaceDTO> getAllPlaces() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get places. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<PlaceDTO>>() {});
    }

    public static PlaceDTO createPlace(PlaceDTO place) throws Exception {
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(place);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting place to JSON", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Failed to create place. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }

        return mapper.readValue(response.body(), PlaceDTO.class);
    }
}
