package pt.ul.fc.css.soccernow.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import pt.ul.fc.css.soccernow.dto.CardRequestDto;
import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.dto.SimpleMatchDto;
import pt.ul.fc.css.soccernow.dto.ScoreRequestDto;
import pt.ul.fc.css.soccernow.exceptions.PlayerNotFoundException;
import pt.ul.fc.css.soccernow.exceptions.RefereeNotFoundException;
import pt.ul.fc.css.soccernow.handlers.MatchHandler;
import pt.ul.fc.css.soccernow.handlers.MatchStatsHandler;
import pt.ul.fc.css.soccernow.handlers.PlayerHandler;

@RestController
@RequestMapping("/api/matches")
@Api(value = "Match API", tags = "Matches")
public class MatchController {

    @Autowired
    private MatchHandler matchHandler;

    @Autowired
    private MatchStatsHandler matchStatsHandler;

    @Autowired
    private PlayerHandler playerHandler;

    @GetMapping
    @ApiOperation(value = "Get all matches", notes = "Returns a list of all matches.")
    public ResponseEntity<List<MatchDto>> getAllMatches(){
        List<MatchDto> matches = matchHandler.getAllMatches();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/by-id/{id}")
    @ApiOperation(value = "id", notes = "Returns a match given its id.")
    public ResponseEntity<MatchDto> getMatchById (@PathVariable("id") long id) {
    	MatchDto matchDto = matchHandler.getMatchById(id);
        if (matchDto != null) {
            return ResponseEntity.ok(matchDto);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ApiOperation(value = "Create match", notes = "Creates a new match and returns the created match DTO.")
    public ResponseEntity<?> createMatch(@RequestBody MatchDto matchDto) {
        try {
            MatchDto responseDto = matchHandler.createMatch(matchDto);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }

    @PostMapping("/simple-create")
    @ApiOperation(value = "Create match with IDs", 
                 notes = "Creates a new match using only IDs of existing entities (place, referee, lineups)")
    public ResponseEntity<?> createSimpleMatch(@RequestBody SimpleMatchDto simpleMatchDto) {
        try {
            MatchDto responseDto = matchHandler.createSimpleMatch(simpleMatchDto);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }

    @PutMapping("/register-score")
    @ApiOperation(value = "Register match score",
            notes = "Registers player goals for a match and calculates team scores automatically")
    public ResponseEntity<?> registerScore(@RequestBody ScoreRequestDto request) {
        try {
            // Validate request
            if (request.getPlayerGoals() == null || request.getPlayerGoals().isEmpty()) {
                return ResponseEntity.badRequest().body("Player goals list cannot be empty");
            }

            MatchDto response = matchHandler.registerScore(
                request.getMatchId(),
                request.getPlayerGoalsAsMap() // Use the helper method
            );
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                .body("Unexpected error: " + ex.getMessage());
        }
    }

    @GetMapping("/player-stats/{playerName}/avg-goals")
    @ApiOperation(value = "Get average goals per game for player",
                 notes = "Returns the average number of goals scored by the specified player per game.")
    public ResponseEntity<?> getAverageGoalsPerPlayer(@PathVariable("playerName") String playerName) {
        try {
            double avgGoals = matchStatsHandler.getAverageGoalsPerGameForPlayer(playerName);
            String response = String.format("%s averages %.2f goals per game.", playerName, avgGoals);
            return ResponseEntity.ok(response);
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/player-stats/{playerName}/num-goals")
    @ApiOperation(value = "Get the total amount of goals of a player",
                 notes = "Returns the total number of goals scored by the specified player.")
    public ResponseEntity<Integer> getNumberGoalsOfPlayer(@PathVariable("playerName") String playerName) {
        try {
            Integer response = matchStatsHandler.getNumberGoalsOfPlayer(playerName);
            return ResponseEntity.ok(response);
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/teams/most-cards")
    @ApiOperation(value = "Get teams with most cards",
                 notes = "Returns teams ordered by total cards (yellow + red) in descending order.")
    public ResponseEntity<?> getTeamsWithMostCards() {
        String result = matchStatsHandler.getTeamsWithMostCards();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/teams/most-wins")
    @ApiOperation(value = "Get teams with most wins",
                 notes = "Returns teams ordered by number of wins in descending order.")
    public ResponseEntity<?> getTeamsWithMostWins() {
        String result = matchStatsHandler.getTeamsWithMostWins();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/referees/most-matches")
    @ApiOperation(value = "Get referee with most matches",
                 notes = "Returns the referee who has officiated the most matches and their total match count.")
    public ResponseEntity<?> getRefereeWithMostMatches() {
        try {
            String result = matchHandler.getRefereeWithMostMatches();
            return ResponseEntity.ok(result);
        }
        catch (RefereeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/players/most-red-cards")
    @ApiOperation(value = "Get players with most red cards",
                 notes = "Returns list of players ordered by their red card count in descending order")
    public ResponseEntity<?> getPlayersWithMostRedCards() {
        try {
            String result = matchStatsHandler.getPlayersWithMostRedCards();
            return ResponseEntity.ok(result);
        }
        catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/players/{playerId}/avg-goals")
    @ApiOperation(value = "Get average goals per game",
                 notes = "Calculates average goals per game for specified player")
    public ResponseEntity<Double> getAverageGoalsPerGame(
            @PathVariable Long playerId) {
        try {
            double avg = matchStatsHandler.getAverageGoalsPerGameForPlayer(
                playerHandler.getPlayerById(playerId).getName());
            return ResponseEntity.ok(avg);
        } catch (PlayerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register-card")
    @ApiOperation(value = "Register card for player in match",
                 notes = "Registers a yellow or red card for a player in a specific match")
    public ResponseEntity<?> registerCard(@RequestBody CardRequestDto request) {
        try {
            String response = matchStatsHandler.registerCard(
                matchHandler.getMatchByIdEntity(request.getMatchId()),
                playerHandler.getPlayerEntityByName(request.getPlayerName()),
                request.isRedCard()
            );
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                   .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                   .body("Unexpected error: " + ex.getMessage());
        }
    }

    public ResponseEntity<List<MatchDto>> searchWithFilters(String matchPlanning, Integer matchGoals, String matchLocation, String matchTime){
        List<MatchDto> matches = matchHandler.searchWithFilters(matchPlanning, matchGoals, matchLocation, matchTime);
        return ResponseEntity.ok(matches);
    }

    public String getMatchPlanning(MatchDto match) {
        return matchHandler.getMatchPlanning(match);
    }

    public int getMatchGoals(MatchDto match) {
        return matchHandler.getMatchGoals(match);
    }

    public String getMatchTimeOfDay(MatchDto match) {
        return matchHandler.getMatchTimeOfDay(match);
    }

    public String getMatchLocation(MatchDto match) {
        return matchHandler.getMatchLocation(match);
    }

    public Integer getNumberOfGamesRefereedByReferee(Long refId) {
        return matchHandler.getNumberOfGamesRefereedByReferee(refId);
    }

    public Integer getNumberOfCardsIssuedByReferee(Long refId) {
        return matchHandler.getNumberOfCardsIssuedByReferee(refId);
    }

    public Long countRedCardsByPlayerId(long playerId) {
        return matchStatsHandler.countRedCardsByPlayerId(playerId);
    }

    public Long countYellowCardsByPlayerId(long playerId) {
        return matchStatsHandler.countYellowCardsByPlayerId(playerId);
    }

    public Integer countMatchesByPlayer(Long playerId) {
        return matchHandler.countMatchesByPlayer(playerId);
    }

    // @DeleteMapping("/matches/{id}")
    // public ResponseEntity<String> deleteMatch(@PathVariable long id) {
    //     boolean deleted = matchHandler.deleteMatch(id);
    //     if (deleted) {
    //         return ResponseEntity.ok("Match deleted successfully");
    //     } else {
    //         return ResponseEntity.notFound().build();
    //     }
    // }
}
