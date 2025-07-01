package pt.ul.fc.css.javafx.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.ul.fc.css.javafx.dto.TournamentDTO;

public class TournamentApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/tournaments";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<TournamentDTO> getAllTournaments() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get tournaments. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<TournamentDTO>>() {});
    }

    public static TournamentDTO createTournament(TournamentDTO tournament) throws Exception {
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(tournament);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting tournament to JSON", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Failed to create tournament. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }

        return mapper.readValue(response.body(), TournamentDTO.class);
    }

    public static TournamentDTO scheduleMatchToTournament(Long tournamentId, Long matchId) throws Exception {
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(Map.of("matchId", matchId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting matchId to JSON", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tournamentId + "/scheduled-matches"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to schedule match to tournament. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }

        return mapper.readValue(response.body(), TournamentDTO.class);
    }

    public static TournamentDTO markMatchAsPlayed(Long tournamentId, Long matchId) throws Exception {
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(Map.of("matchId", matchId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting matchId to JSON", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tournamentId + "/matches/" + matchId + "/mark-played"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to mark match as played. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }

        return mapper.readValue(response.body(), TournamentDTO.class);
    }

    public static TournamentDTO updateTournament(Long tournamentId, TournamentDTO tournament) throws Exception {
        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(tournament);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting tournament to JSON", e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tournamentId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to update tournament. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }

        return mapper.readValue(response.body(), TournamentDTO.class);
    }

    public static void deleteTournament(Long tournamentId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tournamentId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete tournament. HTTP code: " + response.statusCode()
                + "\nResponse: " + response.body());
        }
    }

    public static TournamentDTO getTournamentById(Long tournamentId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + tournamentId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            return null;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get tournament. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), TournamentDTO.class);
    }
}
