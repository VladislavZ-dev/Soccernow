package pt.ul.fc.css.soccernow.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.ul.fc.css.soccernow.dto.TournamentDto;
import pt.ul.fc.css.soccernow.handlers.TournamentHandler;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.TournamentType;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "*")
public class TournamentController {

    @Autowired
    private TournamentHandler tournamentHandler;

    /**
     * Create a new tournament
     * POST /api/tournaments
     */
    @PostMapping
    public ResponseEntity<?> createTournament(@RequestBody CreateTournamentRequest request) {
        try {
            TournamentDto tournament = tournamentHandler.createTournament(
                request.getName(), 
                request.getTeamNames(),
                request.getType()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(tournament);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create tournament: " + e.getMessage()));
        }
    }

    /**
     * Get all tournaments or search tournaments by name
     * GET /api/tournaments?search=searchTerm
     */
    @GetMapping
    public ResponseEntity<?> getTournaments(@RequestParam(required = false) String search) {
        try {
            List<TournamentDto> tournaments = tournamentHandler.searchTournaments(search);
            return ResponseEntity.ok(tournaments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve tournaments: " + e.getMessage()));
        }
    }

    /**
     * Get a single tournament by ID
     * GET /api/tournaments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTournamentById(@PathVariable long id) {
        try {
            Optional<TournamentDto> tournament = tournamentHandler.getTournamentById(id);
            if (tournament.isPresent()) {
                return ResponseEntity.ok(tournament.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve tournament: " + e.getMessage()));
        }
    }

    /**
     * Update a tournament
     * PUT /api/tournaments/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTournament(
            @PathVariable long id, 
            @RequestBody UpdateTournamentRequest request) {
        try {
            TournamentDto updatedTournament = tournamentHandler.updateTournament(
                id, 
                request.getName(), 
                request.getTeamNames(),
                request.getType()
            );
            return ResponseEntity.ok(updatedTournament);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update tournament: " + e.getMessage()));
        }
    }

    /**
     * Delete a tournament
     * DELETE /api/tournaments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTournament(@PathVariable long id) {
        try {
            boolean deleted = tournamentHandler.removeTournament(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete tournament: " + e.getMessage()));
        }
    }

    /**
     * Cancel a scheduled match in a tournament
     * DELETE /api/tournaments/{tournamentId}/matches/{matchId}
     */
    @DeleteMapping("/{tournamentId}/Scheduled-Matches/{matchId}")
    public ResponseEntity<?> cancelScheduledMatch(
            @PathVariable long tournamentId, 
            @PathVariable long matchId) {
        try {
            boolean cancelled = tournamentHandler.cancelScheduledMatch(tournamentId, matchId);
            if (cancelled) {
                return ResponseEntity.ok(matchId);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to cancel match: " + e.getMessage()));
        }
    }

    /**
     * Add a team to a tournament
     * PUT /api/tournaments/{tournamentId}/teams
     */
    @PutMapping("/{tournamentId}/teams")
    public ResponseEntity<?> addTeamToTournament(
            @PathVariable long tournamentId, 
            @RequestBody AddTeamRequest request) {
        try {
            TournamentDto updatedTournament = tournamentHandler.addTeamToTournament(
                tournamentId, 
                request.getTeamName()
            );
            return ResponseEntity.ok(updatedTournament);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add team to tournament: " + e.getMessage()));
        }
    }

    /**
     * Remove a team from a tournament
     * DELETE /api/tournaments/{tournamentId}/teams/{teamName}
     */
    @DeleteMapping("/{tournamentId}/teams/{teamName}")
    public ResponseEntity<?> removeTeamFromTournament(
            @PathVariable long tournamentId, 
            @PathVariable String teamName) {
        try {
            TournamentDto updatedTournament = tournamentHandler.removeTeamFromTournament(
                tournamentId, 
                teamName
            );
            return ResponseEntity.ok(updatedTournament);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to remove team from tournament: " + e.getMessage()));
        }
    }

    /**
     * Add a match to tournament's scheduled matches
     * PUT /api/tournaments/{tournamentId}/scheduled-matches
     */
    @PutMapping("/{tournamentId}/scheduled-matches")
    public ResponseEntity<?> addScheduledMatch(
            @PathVariable long tournamentId, 
            @RequestBody AddMatchRequest request) {
        try {
            TournamentDto updatedTournament = tournamentHandler.addScheduledMatch(
                tournamentId, 
                request.getMatchId()
            );
            return ResponseEntity.ok(updatedTournament);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to add scheduled match: " + e.getMessage()));
        }
    }

    /**
     * Get scheduled matches for a tournament
     * GET /api/tournaments/{tournamentId}/scheduled-matches
     */
    @GetMapping("/{tournamentId}/scheduled-matches")
    public ResponseEntity<?> getScheduledMatches(@PathVariable long tournamentId) {
        try {
            List<Long> scheduledMatches = tournamentHandler.getScheduledMatches(tournamentId);
            return ResponseEntity.ok(scheduledMatches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve scheduled matches: " + e.getMessage()));
        }
    }

    /**
     * Get played matches for a tournament
     * GET /api/tournaments/{tournamentId}/played-matches
     */
    @GetMapping("/{tournamentId}/played-matches")
    public ResponseEntity<?> getPlayedMatches(@PathVariable long tournamentId) {
        try {
            List<Long> playedMatches = tournamentHandler.getPlayedMatches(tournamentId);
            return ResponseEntity.ok(playedMatches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve played matches: " + e.getMessage()));
        }
    }

    /**
     * Mark a scheduled match as played
     * PUT /api/tournaments/{tournamentId}/matches/{matchId}/mark-played
     */
    @PutMapping("/{tournamentId}/matches/{matchId}/mark-played")
    public ResponseEntity<?> markMatchAsPlayed(
            @PathVariable long tournamentId, 
            @PathVariable long matchId) {
        try {
            TournamentDto updatedTournament = tournamentHandler.markMatchAsPlayed(
                tournamentId, 
                matchId
            );
            return ResponseEntity.ok(updatedTournament);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to mark match as played: " + e.getMessage()));
        }
    }

    // Request DTOs
    public static class CreateTournamentRequest {
        private String name;
        private List<String> teamNames;
        private TournamentType type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTeamNames() {
            return teamNames;
        }

        public void setTeamNames(List<String> teamNames) {
            this.teamNames = teamNames;
        }

        public TournamentType getType() {
            return type;
        }

        public void setType(TournamentType type) {
            this.type = type;
        }
    }

    public static class UpdateTournamentRequest {
        private String name;
        private List<String> teamNames;
        private TournamentType type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTeamNames() {
            return teamNames;
        }

        public void setTeamNames(List<String> teamNames) {
            this.teamNames = teamNames;
        }

        public TournamentType getType() {
            return type;
        }

        public void setType(TournamentType type) {
            this.type = type;
        }
    }

    public static class AddTeamRequest {
        private String teamName;

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
    }

    public static class AddMatchRequest {
        private long matchId;

        public long getMatchId() {
            return matchId;
        }

        public void setMatchId(long matchId) {
            this.matchId = matchId;
        }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public ResponseEntity<List<TournamentDto>> searchWithFilters(String tournamentName, String participatingTeam, Integer numToPlayGames,
     Integer numPlayedGames) {
        List<TournamentDto> tournaments = tournamentHandler.searchWithFilters(tournamentName, participatingTeam, numToPlayGames, numPlayedGames);
        return ResponseEntity.ok(tournaments);
    }
}