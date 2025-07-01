package pt.ul.fc.css.javafx.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.ul.fc.css.javafx.dto.RefereeDTO;

public class RefereeApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/referees";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static List<RefereeDTO> getAllReferees() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/referees"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get referees. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<RefereeDTO>>() {});
    }

    public static RefereeDTO createReferee(RefereeDTO referee) throws Exception {
        String requestBody = mapper.writeValueAsString(referee);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/referee"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Failed to create referee. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), RefereeDTO.class);
    }

    public static RefereeDTO updateReferee(RefereeDTO referee) throws Exception {
        String requestBody = mapper.writeValueAsString(referee);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/referee/" + referee.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to update referee. HTTP code: " + response.statusCode());
        }

        return mapper.readValue(response.body(), RefereeDTO.class);
    }

    public static boolean deleteRefereeById(long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/referee/id/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            return false;
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to delete referee. HTTP code: " + response.statusCode() +
                                    ", Response: " + response.body());
        }

        return true;
    }
}
