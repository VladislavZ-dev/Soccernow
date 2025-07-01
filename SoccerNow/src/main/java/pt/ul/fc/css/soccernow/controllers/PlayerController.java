package pt.ul.fc.css.soccernow.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.exceptions.PlayerAlreadyExistsException;
import pt.ul.fc.css.soccernow.exceptions.PlayerNotFoundException;
import pt.ul.fc.css.soccernow.exceptions.TeamNotFoundException;
import pt.ul.fc.css.soccernow.handlers.PlayerHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/players")
@Api(value = "player API", tags = "players")
public class PlayerController {

    @Autowired
    private PlayerHandler playerHandler;

    @GetMapping("/players")
    @ApiOperation(value = "Get all players", notes = "Returns a list of all players.")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerHandler.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/player/id/{id}")
    @ApiOperation(value = "Get player by ID", notes = "Returns a player given their ID number.")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable("id") Long id) {
        try {
            PlayerDTO playerDto = playerHandler.getPlayerById(id);
            return ResponseEntity.ok(playerDto);
        }
        catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/player/{name}")
    @ApiOperation(value = "Get player by name", notes = "Returns a player given their name.")
    public ResponseEntity<PlayerDTO> getplayerByName(@PathVariable("name") String name) {
        try {
            PlayerDTO playerDto = playerHandler.getPlayerByName(name);
            return ResponseEntity.ok(playerDto);
        }
        catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/player/{id}")
    @ApiOperation(value = "Update player by ID", notes = "Updates a player and returns the updated player DTO.")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable("id") Long id, @RequestBody PlayerDTO playerDto) {
        try {
            PlayerDTO dto = playerHandler.updatePlayer(id, playerDto);
            return ResponseEntity.ok(dto);
        }
        catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (TeamNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/player/id/{id}")
    @ApiOperation(value = "Delete player by ID", notes = "Deletes a player given their ID number")
    public ResponseEntity<String> deletePlayerById(@PathVariable("id") Long id) {
        try {
            String response = playerHandler.deletePlayerById(id);
            return ResponseEntity.ok(response);
        }
        catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/player/{name}")
    @ApiOperation(value = "Delete player by name", notes = "Deletes a player given their name")
    public ResponseEntity<String> deletePlayerByName(@PathVariable("name") String name) {
        try {
            String response = playerHandler.deletePlayerByName(name);
            return ResponseEntity.ok(response);
        }
        catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/player")
    @ApiOperation(value = "Create player", notes = "Creates a new player and returns the created player DTO.")
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerDTO playerDto) {
        try {
            PlayerDTO responseDto = playerHandler.createPlayer(playerDto);
            return ResponseEntity.ok(responseDto);
        }
        catch (PlayerAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (TeamNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<List<PlayerDTO>> searchWithFilters(String name, String position, Integer numGoals, Integer numCards, Integer numGames) {
        List<PlayerDTO> players = playerHandler.searchWithFilters(name, position, numGoals, numCards, numGames);
        return ResponseEntity.ok(players);
    }
}
