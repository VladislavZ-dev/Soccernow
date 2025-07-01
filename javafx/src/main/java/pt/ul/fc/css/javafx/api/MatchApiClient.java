package pt.ul.fc.css.javafx.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.ul.fc.css.javafx.dto.MatchDTO;
import pt.ul.fc.css.javafx.dto.MatchGoalsDTO;
import pt.ul.fc.css.javafx.dto.PlayerCardDTO;

public class MatchApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/matches";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<MatchDTO> getAllMatches() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get matches. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<MatchDTO>>() {});
    }

    public static MatchDTO createMatch(MatchDTO match) throws Exception {
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(match);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting match to JSON", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Failed to create match. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }

        return mapper.readValue(response.body(), MatchDTO.class);
    }

    public static MatchDTO getMatchById(Long matchId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/by-id/" + matchId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            return null;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get match. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), MatchDTO.class);
    }

   public static boolean registerGoals(MatchGoalsDTO goalsDTO) throws IOException {
        String jsonInput = new ObjectMapper().writeValueAsString(goalsDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register-score"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return response.statusCode() == 200;
    }

    public static boolean registerCard(PlayerCardDTO cardDTO) throws IOException {
        String jsonInput = new ObjectMapper().writeValueAsString(cardDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register-card"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                .build();

        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return response.statusCode() == 200;
    }
}
