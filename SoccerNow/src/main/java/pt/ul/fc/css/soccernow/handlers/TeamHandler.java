package pt.ul.fc.css.soccernow.handlers;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.entities.History;
import pt.ul.fc.css.soccernow.entities.LineUp;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.MatchStats;
import pt.ul.fc.css.soccernow.entities.Placement;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Position;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.exceptions.TeamAlreadyExistsException;
import pt.ul.fc.css.soccernow.exceptions.TeamHasMatchesToPlayException;
import pt.ul.fc.css.soccernow.exceptions.TeamNotFoundException;
import pt.ul.fc.css.soccernow.exceptions.TeamRequiresANameException;
import pt.ul.fc.css.soccernow.repositories.LineUpRepository;
import pt.ul.fc.css.soccernow.repositories.HistoryRepository;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;
import pt.ul.fc.css.soccernow.repositories.TeamRepository;

@Service
public class TeamHandler {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerHandler playerHandler;

    @Autowired
    private HistoryHandler historyHandler;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private LineUpRepository lineUpRepository;

    @Autowired
    private HistoryRepository historyRepository;

    public List<TeamDto> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(this::mapToDto)
                .toList();
    }

    public TeamDto createNewTeam(TeamDto teamDto) {
        try {
            List<Team> searchedTeam = teamRepository.findByName(teamDto.getName());
            if (!searchedTeam.isEmpty())
                throw new TeamAlreadyExistsException(teamDto.getName());
            if (teamDto.getName() == null)
                throw new TeamRequiresANameException();
            History history = historyHandler.createHistory();
            Team newTeam = new Team(teamDto.getName());
            newTeam.setHistory(history);
            history.setTeam(newTeam);
            Team addedTeam = teamRepository.save(newTeam);
            return new TeamDto(addedTeam.getId(), addedTeam.getName(), addedTeam.getHistory().getId(), getPlayerNames(addedTeam.getPlayers()));
        }
        catch (TeamAlreadyExistsException | TeamRequiresANameException e) {
            System.out.println("Team could not be created with given name");
            return null;
        }
    }

    @Transactional
    public void removeTeam(String name) {
        try {
            TeamDto toDelete = searchTeam(name);
            if (toDelete == null)
                return;
            if (verifyIfTeamHasAppointedMatches(name))
                throw new TeamHasMatchesToPlayException(name);
            removeTeamFromLineUps(name);
            playerHandler.removePlayersFromTeam(name);
            teamRepository.deleteByName(name);
        }
        catch (TeamHasMatchesToPlayException e) {
            System.out.println("It's not possible to delete a Team that has matches to play");
            return;
        }
    }
    @Transactional
    public void removeTeamById(long teamId) {
        Team team = this.teamRepository.findById(teamId)
        .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamId));
        String name = team.getName();
        try {
            TeamDto toDelete = searchTeam(name);
            if (toDelete == null)
                return;
            if (verifyIfTeamHasAppointedMatches(name))
                throw new TeamHasMatchesToPlayException(name);
            removeTeamFromLineUps(name);
            playerHandler.removePlayersFromTeam(name);
            teamRepository.deleteByName(name);
        }
        catch (TeamHasMatchesToPlayException e) {
            System.out.println("It's not possible to delete a Team that has matches to play");
            return;
        }
    }

    public TeamDto updateTeam(TeamDto updatedTeam, String teamName) {
        Team teamToUpdate = searchTeamEntity(teamName);
        if (teamToUpdate == null)
            return null;
        teamToUpdate.setName(updatedTeam.getName());
        teamToUpdate.setPlayers(getPlayerEntities(updatedTeam.getPlayers()));
        return mapToDto(teamRepository.save(teamToUpdate));
    }

    public TeamDto updateTeamById(TeamDto updatedTeam, long teamId) {
        Team team = this.teamRepository.findById(teamId)
        .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamId));
        String teamName = team.getName();
        Team teamToUpdate = searchTeamEntity(teamName);
        if (teamToUpdate == null)
            return null;
        teamToUpdate.setName(updatedTeam.getName());
        teamToUpdate.setPlayers(getPlayerEntities(updatedTeam.getPlayers()));
        return mapToDto(teamRepository.save(teamToUpdate));
    }

    @Transactional
    public TeamDto searchTeam(String teamName) {
        try {
            if (teamName == null || teamName.equals(""))
                throw new TeamNotFoundException();
            List<Team> searchedTeam = teamRepository.findByName(teamName);
            if (searchedTeam.isEmpty()) {
                throw new TeamNotFoundException(teamName);
            }
            return mapToDto(searchedTeam.get(0));
        }
        catch (TeamNotFoundException e) {
            System.out.println("Team could not been found");
            return null;
        }
    }

    @Transactional
    public TeamDto searchTeamById(long teamId) {
        try {
            Team team = this.teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamId));
            String teamName = team.getName();
            if (teamName == null || teamName.equals(""))
                throw new TeamNotFoundException();
            List<Team> searchedTeam = teamRepository.findByName(teamName);
            if (searchedTeam.isEmpty()) {
                throw new TeamNotFoundException(teamName);
            }
            return mapToDto(searchedTeam.get(0));
        }
        catch (TeamNotFoundException e) {
            System.out.println("Team could not been found");
            return null;
        }
    }

    @Transactional
    public Team searchTeamEntity(String teamName) {
        try {
            if (teamName == null || teamName.equals(""))
                throw new TeamNotFoundException();
            List<Team> searchedTeam = teamRepository.findByName(teamName);
            if (searchedTeam.isEmpty()) {
                throw new TeamNotFoundException(teamName);
            }
            return searchedTeam.get(0);
        }
        catch (TeamNotFoundException e) {
            System.out.println("Team could not been found");
            return null;
        }
    }

    public TeamDto mapToDto(Team team) {
        TeamDto newTeamDto = new TeamDto(team.getId(), team.getName(), team.getHistory().getId(), getPlayerNames(team.getPlayers()));
        return newTeamDto;
    }

    public int getNumberOfPlayersOfTeam(String teamName) {
        TeamDto team = searchTeam(teamName);
        return team.getPlayers().size();
    }

    public int getNumberOfPlayersOfTeamById(long teamId) {
        Team teamAux = this.teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamId));
        String teamName = teamAux.getName();
        TeamDto team = searchTeam(teamName);
        return team.getPlayers().size();
    }

    private List<String> getPlayerNames(List<Player> players) {
        if (players == null)
            return null;
        List<String> playerNames = new LinkedList<>();
        for (Player player : players)
            playerNames.add(player.getName());
        return playerNames;
    }

    private List<Player> getPlayerEntities(List<String> names) {
        if (names == null)
            return null;
        List<Player> players = new LinkedList<>();
        for (String name : names)
            players.add(playerRepository.findByName(name).get());
        return players;
    }

    private boolean verifyIfTeamHasAppointedMatches(String teamName) {
        List<Match> futureMatches = matchRepository.findFutureMatches();
        if (futureMatches == null || futureMatches.size() == 0)
            return false;
        for (Match match : futureMatches) {
            if (match.getPlace().getDateTime().isAfter(LocalDateTime.now())) {
                if (match.getLineUp1().getTeam().getName().equals(teamName) || match.getLineUp2().getTeam().getName().equals(teamName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeTeamFromLineUps(String teamName) {
        List<LineUp> lineUps = lineUpRepository.findLineUpsByTeamName(teamName);
        for (LineUp lineUp : lineUps) {
            lineUp.setTeam(null);
            lineUpRepository.save(lineUp);
        }
    }

    public List<TeamDto> searchWithFilters(String name, Integer numPlayers, Integer numWins, Integer numDraws, Integer numLosses,
     String placement, String missingPosition) {
        List<Team> teams;
        if (name == null || name.equals(""))
            teams = teamRepository.findAll();
        else 
            teams = teamRepository.findByName(name);
        List<TeamDto> matchingNameTeams = teams.stream().map(this::mapToDto).toList();
        List<TeamDto> matchingTeams = new LinkedList<>();
        for (TeamDto team : matchingNameTeams) {
            if (numPlayers != null && getNumberOfPlayersOfTeam(team.getName()) != numPlayers)
                continue;
            if (numWins != null && countWinsOfTeam(team.getName()) != numWins)
                continue;
            if (numDraws != null && countDrawsOfTeam(team.getName()) != numDraws)
                continue;
            if (numLosses != null && countLossesOfTeam(team.getName()) != numLosses)
                continue;  
            if (!placement.equals("") && !hasTrophy(placement, team))
                continue;           
            if (!missingPosition.equals("") && !positionIsMissingInTeam(getPlayerEntities(team.getPlayers()), missingPosition))
                continue;
            matchingTeams.add(team);
        }
        return matchingTeams;
    }

    private boolean hasTrophy(String placement, TeamDto teamDto  ) {
        History history = this.historyRepository.findById(teamDto.getHistoryId())
        .orElseThrow(() -> new IllegalArgumentException("History not found with ID: " + teamDto.getHistoryId() ));

        if(placement.equals("participant")){
           if( history.getTournamentPlacements().containsValue(Placement.FIRST) ||  history.getTournamentPlacements().containsValue(Placement.SECOND)
             || history.getTournamentPlacements().containsValue(Placement.THIRD)){
                return false;
             }
             else return true;
        }
        return history.getTournamentPlacements().containsValue(Placement.fromString(placement));
    }

    private boolean positionIsMissingInTeam(List<Player> playersOfTeam, String position) {
        for (Player player : playersOfTeam) 
            if (("no " + Position.toString(player.getPosition())).equals(position))
                return false;
        return true;
    }

    public int countWinsOfTeam(String teamName) {
        int counter = 0;
        Team team = searchTeamEntity(teamName);
        History history = team.getHistory();
        for (MatchStats match : history.getPlayedMatches()) {
            if (!match.getWinner().equals(teamName)) {
                continue;
            }
            counter++;
        }
        return counter;
    }

    public int countDrawsOfTeam(String teamName) {
        int counter = 0;
        Team team = searchTeamEntity(teamName);
        History history = team.getHistory();
        for (MatchStats match : history.getPlayedMatches()) {
            if (!match.getWinner().equals("Draw")) {
                continue;
            }
            counter++;
        }
        return counter;
    }

    public int countLossesOfTeam(String teamName) {
        int counter = 0;
        Team team = searchTeamEntity(teamName);
        History history = team.getHistory();
        for (MatchStats match : history.getPlayedMatches()) {
            if (match.getWinner() == null || match.getWinner().equals("") || match.getWinner().equals(teamName) ||
             match.getWinner().equals("Draw")) {
                continue;
            }
            counter++;
        }
        return counter;
    }
}
