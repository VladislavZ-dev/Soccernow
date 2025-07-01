package pt.ul.fc.css.javafx.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.ul.fc.css.javafx.dto.PlayerDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PlayerApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/players";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<PlayerDTO> getAllPlayers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/players"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get players. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<PlayerDTO>>() {});
    }

    public static PlayerDTO createPlayer(PlayerDTO playerDTO) throws Exception {
        String jsonBody = mapper.writeValueAsString(playerDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/player"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Failed to create player. HTTP code: " + response.statusCode() +
                                     ", Response: " + response.body());
        }

        return mapper.readValue(response.body(), PlayerDTO.class);
    }

    public static PlayerDTO updatePlayer(PlayerDTO playerDTO) throws Exception {
        String jsonBody = mapper.writeValueAsString(playerDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/player/" + playerDTO.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to update player. HTTP code: " + response.statusCode() +
                                    ", Response: " + response.body());
        }

        return mapper.readValue(response.body(), PlayerDTO.class);
    }

    public static boolean deletePlayerById(long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/player/id/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            return false;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to delete player. HTTP code: " + response.statusCode() +
                                    ", Response: " + response.body());
        }

        return true;
    }
}
