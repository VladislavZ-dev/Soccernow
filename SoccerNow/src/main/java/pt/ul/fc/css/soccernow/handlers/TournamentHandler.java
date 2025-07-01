package pt.ul.fc.css.soccernow.handlers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.aspectj.weaver.patterns.TypePattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.TournamentDto;
import pt.ul.fc.css.soccernow.entities.History;
import pt.ul.fc.css.soccernow.entities.LineUp;
import pt.ul.fc.css.soccernow.entities.Tournament;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.Placement;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Position;
import pt.ul.fc.css.soccernow.entities.TournamentType;
import pt.ul.fc.css.soccernow.repositories.HistoryRepository;
import pt.ul.fc.css.soccernow.repositories.TournamentRepository;
import pt.ul.fc.css.soccernow.handlers.HistoryHandler;
import pt.ul.fc.css.soccernow.handlers.MatchHandler;
import pt.ul.fc.css.soccernow.repositories.TeamRepository;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;

@Component
public class TournamentHandler {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchHandler matchHandler;

    @Autowired
    private HistoryHandler historyHandler;

    /**
     * Creates a new tournament
     */
    @Transactional
    public TournamentDto createTournament(String name, List<String> teamNames, TournamentType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tournament name cannot be empty");
        }

        if (!tournamentRepository.findByExactName(name).isEmpty()) {
            throw new IllegalArgumentException("Tournament with name '" + name + "' already exists");
        }

        Tournament tournament = new Tournament();
        tournament.setName(name);

        if (teamNames != null && !teamNames.isEmpty()) {
            List<Team> teams = new ArrayList<>();
            for (String teamName : teamNames) {
                Optional<Team> foundTeam = teamRepository.findByNameEquals(teamName);
                if (foundTeam.isPresent()) {
                    teams.add(foundTeam.get()); 
                } else {
                    throw new IllegalArgumentException("Team not found with name: " + teamName);
                }
            }
            tournament.setTeams(teams);
        }

        if (type != null && (type == TournamentType.POINTS || type == TournamentType.KNOCKOUT)) {
            tournament.setType(type);
        }

        Tournament savedTournament = tournamentRepository.save(tournament);
        initializeTeamHistoriesForTournament(savedTournament);     
        return convertToDto(savedTournament);
    }

    /**
     * Initialize team histories when tournament is created
     */
    @Transactional  
    private void initializeTeamHistoriesForTournament(Tournament tournament) {
        for (Team team : tournament.getTeams()) {
            try {
                History teamHistory = team.getHistory();
                if (teamHistory != null) {
                    historyHandler.addInitialTournamentPlacement(teamHistory, tournament);
                }
            } catch (Exception e) {
                System.err.println("Error initializing tournament placement for team " + team.getName() + ": " + e.getMessage());
            }
        }
    }
    /**
     * Search for tournaments by name (exact match)
     */
    public List<TournamentDto> searchTournaments(String searchTerm) {
        List<Tournament> tournaments;
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            tournaments = tournamentRepository.findAll();
        } else {
            tournaments = tournamentRepository.findByExactName(searchTerm.trim());
        }

        return tournaments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a single tournament by ID
     */
    public Optional<TournamentDto> getTournamentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }

        return tournamentRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Update tournament information
     */
    @Transactional
    public TournamentDto updateTournament(long id, String newName, List<String> teamNames, TournamentType type) {
        if ((Long) id == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }

        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + id));

        if (newName != null && !newName.trim().isEmpty()) {
            List<Tournament> existingTournaments = tournamentRepository.findByExactName(newName);
            if (!existingTournaments.isEmpty() && !((Long) id).equals((Long) existingTournaments.get(0).getId())) {
                throw new IllegalArgumentException("Tournament with name '" + newName + "' already exists");
            }
            tournament.setName(newName);
        }
     
        if (teamNames != null) {
            List<Team> teams = new ArrayList<>();
            for (String teamName : teamNames) {
                Optional<Team> foundTeam = teamRepository.findByNameEquals(teamName);
                if (foundTeam.isPresent()) {
                    teams.add(foundTeam.get()); 
                } else {
                    throw new IllegalArgumentException("Team not found with name: " + teamName);
                }
            }
            tournament.setTeams(teams);
        }

        if (type != null && (type == TournamentType.POINTS || type == TournamentType.KNOCKOUT)) {
            tournament.setType(type);
        }

        Tournament updatedTournament = tournamentRepository.save(tournament);
        return convertToDto(updatedTournament);
    }

    @Transactional
    public boolean removeTournament(long id) {
        if ((Long) id == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }

        Tournament tournament = tournamentRepository.findById(id).orElse(null);
        if (tournament == null) {
            return false;
        }

        if (!tournament.getPlayedMatches().isEmpty()) {
            throw new IllegalArgumentException("Cannot remove tournament with played matches. Tournament has " + 
                tournament.getPlayedMatches().size() + " played match(es)");
        }
        List <History> histories = this.historyRepository.findByTournamentInPlacements(tournament);
        for(History history : histories){
            history.getTournamentPlacements().remove(tournament, history.getTournamentPlacements().get(tournament));
            this.historyRepository.save(history);
            Team team =  history.getTeam();
            team.getTournaments().remove(tournament);
            this.teamRepository.save(team);
        }

        // In removeTournament():
        List<Match> matchesToCancel = new ArrayList<>(tournament.getScheduledMatches());
        for (Match matchToCancel : matchesToCancel) {
            matchToCancel.setTournament(null);
            matchRepository.save(matchToCancel);
            matchHandler.deleteMatch(matchToCancel.getId());
        }
        tournament.getScheduledMatches().clear();

        tournamentRepository.delete(tournament);
        return true;
    }

    /**
     * Cancel a scheduled match from a tournament
     */
    @Transactional
    public boolean cancelScheduledMatch(long tournamentId, long matchId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        Match matchToCancel = this.matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));

        if (tournament.getPlayedMatches().contains(matchToCancel)) {
            throw new IllegalArgumentException("A match that has already been played cannot be deleted");
        }

        if (!tournament.getScheduledMatches().contains(matchToCancel)) {
            throw new IllegalArgumentException("Scheduled match not found in tournament");
        }

        matchToCancel.setTournament(null);
        tournament.getScheduledMatches().remove(matchToCancel);
        tournamentRepository.save(tournament);

        matchHandler.deleteMatch(matchToCancel.getId());
        return true;
    }

    /**
     * Get all tournaments
     */
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Add a team to an existing tournament
     */
    @Transactional
    public TournamentDto addTeamToTournament(Long tournamentId, String teamName) {
        if (tournamentId == null || teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tournament ID and team name cannot be null or empty");
        }

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        Optional<Team> foundTeam = teamRepository.findByNameEquals(teamName);
        if (foundTeam.isEmpty()) {
            throw new IllegalArgumentException("Team not found with name: " + teamName);
        }
        Team team = foundTeam.get();

        if (!tournament.getTeams().contains(team)) {
            tournament.addTeam(team);
            tournamentRepository.save(tournament);
            
            // Add initial placement to team history
            if (team.getHistory() != null) {
                historyHandler.addInitialTournamentPlacement(team.getHistory(), tournament);
            }
        }

        return convertToDto(tournament);
    }

    /**
     * Remove a team from an existing tournament
     * RESTRICTION 1: Cannot remove team if it has played matches in this tournament
     */
    @Transactional
    public TournamentDto removeTeamFromTournament(Long tournamentId, String teamName) {
        if (tournamentId == null || teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tournament ID and team name cannot be null or empty");
        }

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        Optional<Team> foundTeam = teamRepository.findByNameEquals(teamName);
        if (foundTeam.isEmpty()) {
            throw new IllegalArgumentException("Team not found with name: " + teamName);
        }
        Team team = foundTeam.get();

        // RESTRICTION 1: Check if team has played matches in this tournament
        boolean teamHasPlayedMatches = tournament.getPlayedMatches().stream()
                .anyMatch(match -> {
                    String team1Name = getTeamNameFromLineUp(match.getLineUp1());
                    String team2Name = getTeamNameFromLineUp(match.getLineUp2());
                    return teamName.equals(team1Name) || teamName.equals(team2Name);
                });

        if (teamHasPlayedMatches) {
            throw new IllegalArgumentException("Cannot remove team '" + teamName + 
                "' from tournament as it has already played matches in this tournament");
        }

        tournament.removeTeam(team);
        tournamentRepository.save(tournament);

        return convertToDto(tournament);
    }

    /**
     * Convert Tournament entity to TournamentDto
     */
    private TournamentDto convertToDto(Tournament tournament) {
        TournamentDto dto = new TournamentDto();
        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setType(tournament.getType());
        
        List<String> teamNames = tournament.getTeams().stream()
                .map(Team::getName)
                .collect(Collectors.toList());
        dto.setTeamsNames(teamNames);
        
        List<Long> scheduledMatchIds = tournament.getScheduledMatches().stream()
                .map(Match::getId)
                .collect(Collectors.toList());
        dto.setScheduledMatchesIds(scheduledMatchIds);
        
        List<Long> playedMatchIds = tournament.getPlayedMatches().stream()
                .map(Match::getId)
                .collect(Collectors.toList());
        dto.setPlayedMatchesIds(playedMatchIds);
        
        Map<String, String> teamsPlacements = new HashMap<>();
        for (Team team : tournament.getTeams()) {
            teamsPlacements.put(team.getName(), Placement.toString(Placement.PARTICIPANT));
        }
        
        dto.setTeamsPlacements(teamsPlacements);
        return dto; 
    }

    /**
     * Helper method to safely update team history placement using Tournament entity
     */
    private void updateTeamHistoryPlacement(String teamName, Tournament tournament, Placement placement) {
        try {
            History teamHistory = getHistoryByTeamNameSafe(teamName);
            if (teamHistory != null) {
                this.historyHandler.updateTournamentPlacement(teamHistory, tournament, placement);
            }
        } catch (Exception e) {
            System.err.println("Error updating tournament placement for team " + teamName + ": " + e.getMessage());
        }
    }

    /**
     * Helper method to check if a match already belongs to any tournament
     */
    private boolean isMatchAlreadyInAnyTournament(Long matchId) {
        List<Tournament> allTournaments = tournamentRepository.findAll();
        for (Tournament tournament : allTournaments) {
            boolean inScheduled = tournament.getScheduledMatches().stream()
                    .anyMatch(match -> matchId.equals((Long) match.getId()));
            boolean inPlayed = tournament.getPlayedMatches().stream()
                    .anyMatch(match -> matchId.equals((Long) match.getId()));
            
            if (inScheduled || inPlayed) {
                return true;
            }
        }  
        return false;
    }

    /**
     * Add a match to tournament's scheduled matches
     */
    @Transactional
    public TournamentDto addScheduledMatch(Long tournamentId, Long matchId) {
        if (tournamentId == null || matchId == null) {
            throw new IllegalArgumentException("Tournament ID and Match ID cannot be null");
        }

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));

        if (isMatchAlreadyInAnyTournament(matchId)) {
            throw new IllegalArgumentException("Match with ID " + matchId + 
                " already belongs to another tournament and cannot be added");
        }

        if (tournament.getScheduledMatches().contains(match)) {
            throw new IllegalArgumentException("Match is already scheduled for this tournament");
        }
        if (tournament.getPlayedMatches().contains(match)) {
            throw new IllegalArgumentException("Match has already been played in this tournament");
        }

        tournament.addScheduledMatch(match);
        match.setTournament(tournament);
        Tournament savedTournament = tournamentRepository.save(tournament);
        return convertToDto(savedTournament);
    }

    /**
     * Get all scheduled matches for a tournament
     */
    public List<Long> getScheduledMatches(Long tournamentId) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        return tournament.getScheduledMatches().stream()
                .map(Match::getId)
                .collect(Collectors.toList());
    }

    /**
     * Get all played matches for a tournament  
     */
    public List<Long> getPlayedMatches(Long tournamentId) {
        if (tournamentId == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        return tournament.getPlayedMatches().stream()
                .map(Match::getId)
                .collect(Collectors.toList());
    }

    /**
     * Move a match from scheduled to played matches
     */

    @Transactional
    public TournamentDto markMatchAsPlayed(long tournamentId, long matchId) {
        if ((Long) tournamentId == null || (Long) matchId == null) {
            throw new IllegalArgumentException("Tournament ID and Match ID cannot be null");
        }

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        Match match = null;
        for (Match scheduledMatch : tournament.getScheduledMatches()) {
            if (((Long) matchId).equals((Long) scheduledMatch.getId())) {
                match = scheduledMatch;
                break;
            }
        }

        if (match == null) {
            throw new IllegalArgumentException("Scheduled match not found in tournament");
        }

        String team1Name = getTeamNameFromLineUp(match.getLineUp1());
        String team2Name = getTeamNameFromLineUp(match.getLineUp2());
        
        Set<String> tournamentTeamNames = tournament.getTeams().stream()
                .map(Team::getName)
                .collect(Collectors.toSet());
        
        if (!"Unknown Team".equals(team1Name) && !tournamentTeamNames.contains(team1Name)) {
            throw new IllegalArgumentException("Team '" + team1Name + "' is not a participant in this tournament");
        }
        
        if (!"Unknown Team".equals(team2Name) && !tournamentTeamNames.contains(team2Name)) {
            throw new IllegalArgumentException("Team '" + team2Name + "' is not a participant in this tournament");
        }
        
        tournament.getScheduledMatches().remove(match);
        tournament.addPlayedMatch(match);

        Tournament savedTournament = tournamentRepository.save(tournament);

        updateTournamentPlacements(savedTournament);

        return convertToDto(savedTournament);
    }

    @Transactional
    private void updateTournamentPlacements(Tournament tournament) {
        try {
            if (tournament.getType() == TournamentType.POINTS) {
                updatePointsBasedPlacements(tournament);
            } else if (tournament.getType() == TournamentType.KNOCKOUT) {
                updateKnockoutBasedPlacements(tournament);
            }
        } catch (Exception e) {
            System.err.println("Error updating tournament placements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update placements for points-based tournament
     */
    private void updatePointsBasedPlacements(Tournament tournament) {
        Map<String, Integer> teamPoints = new HashMap<>();
        Map<String, Integer> teamWins = new HashMap<>();
        Map<String, Integer> teamLosses = new HashMap<>();
        Map<String, Integer> teamDraws = new HashMap<>();

        // Initialize all teams with 0 points
        for (Team team : tournament.getTeams()) {
            String teamName = team.getName();
            teamPoints.put(teamName, 0);
            teamWins.put(teamName, 0);
            teamLosses.put(teamName, 0);
            teamDraws.put(teamName, 0);
        }

        // Calculate points from played matches
        for (Match match : tournament.getPlayedMatches()) {
            String team1Name = getTeamNameFromLineUp(match.getLineUp1());
            String team2Name = getTeamNameFromLineUp(match.getLineUp2());

            if ("Unknown Team".equals(team1Name) || "Unknown Team".equals(team2Name)) {
                continue;
            }

            if (match.getStats() != null && match.getStats().getWinner() != null) {
                String winner = match.getStats().getWinner();

                if ("Draw".equals(winner)) {
                    teamPoints.put(team1Name, teamPoints.getOrDefault(team1Name, 0) + 1);
                    teamPoints.put(team2Name, teamPoints.getOrDefault(team2Name, 0) + 1);
                    teamDraws.put(team1Name, teamDraws.getOrDefault(team1Name, 0) + 1);
                    teamDraws.put(team2Name, teamDraws.getOrDefault(team2Name, 0) + 1);
                } else if (team1Name.equals(winner)) {
                    teamPoints.put(team1Name, teamPoints.getOrDefault(team1Name, 0) + 3);
                    teamWins.put(team1Name, teamWins.getOrDefault(team1Name, 0) + 1);
                    teamLosses.put(team2Name, teamLosses.getOrDefault(team2Name, 0) + 1);
                } else if (team2Name.equals(winner)) {
                    teamPoints.put(team2Name, teamPoints.getOrDefault(team2Name, 0) + 3);
                    teamWins.put(team2Name, teamWins.getOrDefault(team2Name, 0) + 1);
                    teamLosses.put(team1Name, teamLosses.getOrDefault(team1Name, 0) + 1);
                }
            }
        }
        
        // Sort teams by points (descending), then by wins (descending) as tiebreaker
        List<Map.Entry<String, Integer>> sortedTeams = teamPoints.entrySet().stream()
                .sorted((e1, e2) -> {
                    int pointsComparison = e2.getValue().compareTo(e1.getValue());
                    if (pointsComparison != 0) {
                        return pointsComparison;
                    }
                    return teamWins.get(e2.getKey()).compareTo(teamWins.get(e1.getKey()));
                })
                .collect(Collectors.toList());

        // Track which teams have played at least one match
        Set<String> teamsWithMatches = new HashSet<>();
        for (Match match : tournament.getPlayedMatches()) {
            String team1Name = getTeamNameFromLineUp(match.getLineUp1());
            String team2Name = getTeamNameFromLineUp(match.getLineUp2());
            
            if (!"Unknown Team".equals(team1Name)) {
                teamsWithMatches.add(team1Name);
            }
            if (!"Unknown Team".equals(team2Name)) {
                teamsWithMatches.add(team2Name);
            }
        }
        
        for (int i = 0; i < sortedTeams.size(); i++) {
            String teamName = sortedTeams.get(i).getKey();

            Placement placement;
            if (!teamsWithMatches.contains(teamName)) {
                // Team hasn't played any matches, so they're just a participant
                placement = Placement.PARTICIPANT;
            } else if (i == 0) {
                // First place (team has played matches)
                placement = Placement.FIRST;
            } else if (i == 1 && sortedTeams.size() > 1) {
                // Second place (team has played matches)
                placement = Placement.SECOND;
            } else if (i == 2 && sortedTeams.size() > 2) {
                // Third place (team has played matches)
                placement = Placement.THIRD;
            } else {
                placement = Placement.PARTICIPANT;
            }
            updateTeamHistoryPlacement(teamName, tournament, placement);
        }
    }
    
    /**
     * Calculate team placements for knockout tournament
     * Only winning teams advance to next round
     */
    private void updateKnockoutBasedPlacements(Tournament tournament) {
        List<Match> playedMatches = tournament.getPlayedMatches();
        
        // Validate no draws exist in knockout matches
        validateNoDraws(playedMatches);
        
        if (playedMatches.isEmpty()) {
            return;
        }
        
        // Get all teams participating
        Set<String> allTeams = getAllTeamsFromMatches(playedMatches);
        
        // Calculate total rounds needed (log2 of team count)
        int totalRounds = (int) Math.ceil(Math.log(allTeams.size()) / Math.log(2));
        
        // Track wins and losses for each team
        Map<String, Integer> teamWins = new HashMap<>();
        Map<String, Integer> teamLosses = new HashMap<>();
        Map<String, List<String>> teamBeatenBy = new HashMap<>(); // who beat this team
        Map<String, List<String>> teamBeat = new HashMap<>(); // who this team beat
        
        // Initialize all teams
        for (String team : allTeams) {
            teamWins.put(team, 0);
            teamLosses.put(team, 0);
            teamBeatenBy.put(team, new ArrayList<>());
            teamBeat.put(team, new ArrayList<>());
        }
        
        // Process all matches to count wins/losses
        for (Match match : playedMatches) {
            String team1 = getTeamNameFromLineUp(match.getLineUp1());
            String team2 = getTeamNameFromLineUp(match.getLineUp2());
            String winner = match.getStats().getWinner();
            String loser = winner.equals(team1) ? team2 : team1;
            
            // Update win/loss counts
            teamWins.put(winner, teamWins.get(winner) + 1);
            teamLosses.put(loser, teamLosses.get(loser) + 1);
            
            // Track who beat whom
            teamBeat.get(winner).add(loser);
            teamBeatenBy.get(loser).add(winner);
        }
        
        // Find champion (team with maximum wins and no losses, or won all required rounds)
        String champion = findChampion(teamWins, teamLosses, totalRounds);
        if (champion != null) {
            updateTeamHistoryPlacement(champion, tournament.getName(), Placement.FIRST);
        }
        
        // Find runner-up (team that lost only to the champion in the final)
        String runnerUp = findRunnerUp(champion, teamBeatenBy, teamWins, totalRounds);
        if (runnerUp != null) {
            updateTeamHistoryPlacement(runnerUp, tournament.getName(), Placement.SECOND);
        }
        
        // Find third place
        String thirdPlace = findThirdPlace(champion, runnerUp, teamWins, teamLosses, teamBeatenBy, playedMatches);
        if (thirdPlace != null) {
            updateTeamHistoryPlacement(thirdPlace, tournament.getName(), Placement.THIRD);
        }
        
        // All remaining teams get PARTICIPANT placement
        Set<String> processedTeams = Set.of(champion, runnerUp, thirdPlace);
        for (String team : allTeams) {
            if (!processedTeams.contains(team)) {
                updateTeamHistoryPlacement(team, tournament.getName(), Placement.PARTICIPANT);
            }
        }
    }

    /**
     * Get all unique team names from played matches
     */
    private Set<String> getAllTeamsFromMatches(List<Match> matches) {
        Set<String> teams = new HashSet<>();
        for (Match match : matches) {
            teams.add(getTeamNameFromLineUp(match.getLineUp1()));
            teams.add(getTeamNameFromLineUp(match.getLineUp2()));
        }
        return teams;
    }

    /**
     * Find the champion - team that won all their matches and has the required number of wins
     */
    private String findChampion(Map<String, Integer> teamWins, Map<String, Integer> teamLosses, int totalRounds) {
        for (Map.Entry<String, Integer> entry : teamWins.entrySet()) {
            String team = entry.getKey();
            int wins = entry.getValue();
            int losses = teamLosses.get(team);
            
            // Champion should have won all required rounds and have no losses (or minimal losses if bracket allows)
            if (losses == 0 && wins == totalRounds) {
                return team;
            }
        }
        
        // If no perfect record, find team with most wins and minimal losses
        return teamWins.entrySet().stream()
                .filter(entry -> teamLosses.get(entry.getKey()) <= 1) // At most 1 loss
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Find runner-up - team that made it to final but lost to champion
     */
    private String findRunnerUp(String champion, Map<String, List<String>> teamBeatenBy, 
                            Map<String, Integer> teamWins, int totalRounds) {
        if (champion == null) return null;
        
        // Runner-up should have been beaten by champion and have high win count
        for (Map.Entry<String, List<String>> entry : teamBeatenBy.entrySet()) {
            String team = entry.getKey();
            List<String> beatBy = entry.getValue();
            
            // If this team was beaten by champion and has second-highest wins
            if (beatBy.contains(champion) && teamWins.get(team) == totalRounds - 1) {
                return team;
            }
        }
        
        return null;
    }

    /**
     * Find third place - could be from a third-place match or semifinal loser with most wins
     */
    private String findThirdPlace(String champion, String runnerUp, Map<String, Integer> teamWins, 
                                Map<String, Integer> teamLosses, Map<String, List<String>> teamBeatenBy,
                                List<Match> playedMatches) {
        if (champion == null || runnerUp == null) return null;
        
        // Find teams that lost in semifinals (beaten by champion or runner-up)
        List<String> semifinalLosers = new ArrayList<>();
        
        for (Map.Entry<String, List<String>> entry : teamBeatenBy.entrySet()) {
            String team = entry.getKey();
            List<String> beatBy = entry.getValue();
            
            // Skip if this is champion or runner-up
            if (team.equals(champion) || team.equals(runnerUp)) continue;
            
            // If beaten by champion or runner-up, could be semifinal loser
            if (beatBy.contains(champion) || beatBy.contains(runnerUp)) {
                semifinalLosers.add(team);
            }
        }
        
        // Check if there's a third-place match between semifinal losers
        if (semifinalLosers.size() >= 2) {
            Match thirdPlaceMatch = findMatchBetween(semifinalLosers.get(0), semifinalLosers.get(1), playedMatches);
            if (thirdPlaceMatch != null) {
                return thirdPlaceMatch.getStats().getWinner();
            }
        }
        
        // If no third-place match, return semifinal loser with most wins
        return semifinalLosers.stream()
                .max((t1, t2) -> Integer.compare(teamWins.get(t1), teamWins.get(t2)))
                .orElse(null);
    }

    /**
     * Find match between two specific teams
     */
    private Match findMatchBetween(String team1, String team2, List<Match> matches) {
        if (team1 == null || team2 == null) {
            return null;
        }
        
        for (Match match : matches) {
            String matchTeam1 = getTeamNameFromLineUp(match.getLineUp1());
            String matchTeam2 = getTeamNameFromLineUp(match.getLineUp2());
            
            if ((team1.equals(matchTeam1) && team2.equals(matchTeam2)) ||
                (team1.equals(matchTeam2) && team2.equals(matchTeam1))) {
                return match;
            }
        }
        return null;
    }

    /**
     * Validate that no draws exist in knockout matches
     */
    private void validateNoDraws(List<Match> matches) {
        for (Match match : matches) {
            if (match.getStats() != null && "Draw".equals(match.getStats().getWinner())) {
                throw new IllegalStateException("Draw found in knockout match - please update the score");
            }
        }
    }

    /**
     * Helper method to safely update team history placement
     */
    private void updateTeamHistoryPlacement(String teamName, String tournamentName, Placement placement) {
        if (teamName == null) return;
        
        try {
            History teamHistory = getHistoryByTeamNameSafe(teamName);
            if (teamHistory != null) {
                this.historyHandler.updateTournamentPlacement(
                    teamHistory, 
                    tournamentName, 
                    Placement.toString(placement)
                );
            }
        } catch (Exception e) {
            System.err.println("Error updating tournament placement for team " + teamName + ": " + e.getMessage());
        }
    }
        private History getHistoryByTeamNameSafe(String teamName) { 
        try {
            Optional<Team> teamOpt = this.teamRepository.findByNameEquals(teamName);
            if (teamOpt.isPresent()) {
                Team team = teamOpt.get();
                return team.getHistory(); // This might still be null, but we handle it in the caller
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error fetching history for team " + teamName + ": " + e.getMessage());
            return null;
        }
    }

        /**
         * Helper method to extract team name from lineup
         * You'll need to implement this based on your LineUp structure
         */
        private String getTeamNameFromLineUp(LineUp lineUp) {
        try {
            if (lineUp != null && lineUp.getTeam() != null && lineUp.getTeam().getName() != null) {
                return lineUp.getTeam().getName();
            }
        } catch (Exception e) {
            System.err.println("Error getting team name from lineup: " + e.getMessage());
        }
        return "Unknown Team";
    }



    public List<TournamentDto> searchWithFilters(String tournamentName, String participatingTeam, Integer numToPlayGames, Integer numPlayedGames) {
        List<Tournament> tournaments = tournamentRepository.findWithFilters(
            (tournamentName == null || tournamentName.trim().isEmpty()) ? null : tournamentName,
            (participatingTeam == null || participatingTeam.trim().isEmpty()) ? null : participatingTeam
        );
        
        return tournaments.stream()
            .map(this::convertToDto)
            .filter(tournament -> {
                if (numToPlayGames != null && tournament.getScheduledMatchesIds().size() != numToPlayGames) {
                    return false;
                }
                if (numPlayedGames != null && tournament.getPlayedMatchesIds().size() != numPlayedGames) {
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());
    }
}