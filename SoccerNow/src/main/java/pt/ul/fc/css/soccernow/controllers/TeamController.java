package pt.ul.fc.css.soccernow.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.handlers.TeamHandler;

@RestController
@RequestMapping("/teams")
@Api(value = "Teams API", tags = "Teams")
public class TeamController {

    @Autowired
    private TeamHandler teamHandler;

    @GetMapping
    @ApiOperation(value = "Get all Teams", notes = "Returns a list of all the teams present in soccernow")
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        List<TeamDto> teams = teamHandler.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @PostMapping("/team")
    @ApiOperation(value = "Creates a new Team", notes = "Returns the Dto of the newly created Team")
    public ResponseEntity<TeamDto> createNewTeam(@RequestBody TeamDto team) {
        TeamDto addedTeam = teamHandler.createNewTeam(team);
        return ResponseEntity.ok(addedTeam);
    }

    @DeleteMapping("/team/{teamName}")
    @ApiOperation(value = "Removes a Team", notes = "Deletes the record of a Team from the database")
    public ResponseEntity<String> removeTeam(@PathVariable("teamName") String name) {
        teamHandler.removeTeam(name);
        return ResponseEntity.ok(name + "no longer exists");
    }

    @PutMapping("/team/{teamName}")
    @ApiOperation(value = "Updates an existing Team", notes = "Returns the Dto of the updated Team")
    public ResponseEntity<TeamDto> updateTeam(@RequestBody TeamDto team, @PathVariable("teamName") String teamName) {
        TeamDto updatedTeam = teamHandler.updateTeam(team, teamName);
        return ResponseEntity.ok(updatedTeam);
    }

    @GetMapping("/team/{teamName}")
    @ApiOperation(value = "Searches for a Team", notes = "Returns the Dto of the Team that was searched")
    public ResponseEntity<TeamDto> searchTeam(@PathVariable("teamName") String teamName) {
        TeamDto searchedTeam = teamHandler.searchTeam(teamName);
        return ResponseEntity.ok(searchedTeam);
    }

    @GetMapping("/team/numberPlayers/{teamName}")
    @ApiOperation(value = "Calculates the number of players of a team", notes = "Returns a number of players of the given team")
    public int getNumberOfPlayers(@PathVariable("teamName") String teamName) {
        return teamHandler.getNumberOfPlayersOfTeam(teamName);
    }

    @DeleteMapping("/team/by-id/{teamId}")
    @ApiOperation(value = "Removes a Team", notes = "Deletes the record of a Team from the database")
    public ResponseEntity<String> removeTeamById(@PathVariable("teamId") long teamId) {
        teamHandler.removeTeamById(teamId);
        return ResponseEntity.ok("Team with id "+ teamId + " no longer exists");
    }

    @PutMapping("/team/by-id/{teamId}")
    @ApiOperation(value = "Updates an existing Team", notes = "Returns the Dto of the updated Team")
    public ResponseEntity<TeamDto> updateTeamById(@RequestBody TeamDto team, @PathVariable("teamId") long teamId) {
        TeamDto updatedTeam = teamHandler.updateTeamById(team, teamId);
        return ResponseEntity.ok(updatedTeam);
    }

    @GetMapping("/team/by-id/{teamId}")
    @ApiOperation(value = "Searches for a Team", notes = "Returns the Dto of the Team that was searched")
    public ResponseEntity<TeamDto> searchTeamById(@PathVariable("teamId") long teamId) {
        TeamDto searchedTeam = teamHandler.searchTeamById(teamId);
        return ResponseEntity.ok(searchedTeam);
    }

    @GetMapping("/team/numberPlayers/by-id/{teamId}")
    @ApiOperation(value = "Calculates the number of players of a team", notes = "Returns a number of players of the given team")
    public int getNumberOfPlayersById(@PathVariable("teamId") long teamId) {
        return teamHandler.getNumberOfPlayersOfTeamById(teamId);
    }

    public ResponseEntity<List<TeamDto>> searchWithFilters(String name, Integer numPlayers, Integer numWins, Integer numDraws, Integer numLosses,
     String trophy, String missingPosition) {
        List<TeamDto> teams = teamHandler.searchWithFilters(name, numPlayers, numWins, numDraws, numLosses, trophy, missingPosition);
        return ResponseEntity.ok(teams);
    }

    public int countWinsOfTeam(String teamName) {
        return teamHandler.countWinsOfTeam(teamName);
    }

    public int countDrawsOfTeam(String teamName) {
        return teamHandler.countDrawsOfTeam(teamName);
    }

    public int countLossesOfTeam(String teamName) {
        return teamHandler.countLossesOfTeam(teamName);
    }

}
