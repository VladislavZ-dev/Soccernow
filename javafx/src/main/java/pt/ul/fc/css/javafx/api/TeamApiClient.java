package pt.ul.fc.css.javafx.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.ul.fc.css.javafx.dto.TeamDTO;

public class TeamApiClient {
    private static final String BASE_URL = "http://localhost:8080/teams";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<TeamDTO> getAllTeams() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get teams. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<TeamDTO>>() {});
    }

    public static TeamDTO createTeam(TeamDTO team) throws Exception {
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(team);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting team to JSON", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/team"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Failed to create team. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }

        return mapper.readValue(response.body(), TeamDTO.class);
    }

    public static void deleteTeam(Long teamId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/team/by-id/" + teamId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete team. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }
    }

    public static TeamDTO updateTeam(Long teamId, TeamDTO team) throws Exception {
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(team);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting team to JSON", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/team/by-id/" + teamId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to update team. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }

        return mapper.readValue(response.body(), TeamDTO.class);
    }
}
