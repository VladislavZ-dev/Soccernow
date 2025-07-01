package pt.ul.fc.css.soccernow.handlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.builders.LineUpBuilder;
import pt.ul.fc.css.soccernow.dto.LineUpDto;
import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.dto.SimpleMatchDto;
import pt.ul.fc.css.soccernow.entities.History;
import pt.ul.fc.css.soccernow.entities.LineUp;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.MatchStats;
import pt.ul.fc.css.soccernow.entities.Place;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Referee;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.entities.Tournament;
import pt.ul.fc.css.soccernow.exceptions.RefereeNotFoundException;
import pt.ul.fc.css.soccernow.repositories.LineUpRepository;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;
import pt.ul.fc.css.soccernow.repositories.MatchStatsRepository;
import pt.ul.fc.css.soccernow.repositories.PlaceRepository;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;
import pt.ul.fc.css.soccernow.repositories.RefereeRepository;
import pt.ul.fc.css.soccernow.repositories.TeamRepository;
import pt.ul.fc.css.soccernow.repositories.TournamentRepository;

@Service
public class MatchHandler {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private RefereeRepository refereeRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private LineUpBuilder lineUpBuilder;

    @Autowired
    private LineUpRepository lineUpRepository;

    @Autowired
    private MatchStatsHandler matchStatsHandler;

    @Autowired 
    private MatchStatsRepository matchStatsRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;


    public List<MatchDto> getAllMatches() {
        List<Match> Matches = matchRepository.findAll();
        return Matches.stream()
                .map(this::mapToDto)
                .toList();
    }

    public MatchDto saveMatch(Match Match) {
        return mapToDto(matchRepository.save(Match));
    }

    public MatchDto getMatchById(long id) {
        Optional<Match> MatchOptional = matchRepository.findById(id);   
        if (MatchOptional.isEmpty()) {
            return null;
        }
        return mapToDto(MatchOptional.get());
    }

    public Match getMatchByIdEntity(long id) {
        Optional<Match> MatchOptional = matchRepository.findById(id);
        if (MatchOptional.isEmpty()) {
            return null;
        }
        return MatchOptional.get();
    }

    private LineUpDto mapLineUpToDto(LineUp lineUp) {
        if (lineUp == null) return null;

        LineUpDto dto = new LineUpDto();
        dto.setTeam(lineUp.getTeam().getName());
        dto.setCaptain(lineUp.getCaptain().getName());
        dto.setPivot(lineUp.getPivot().getName());
        dto.setLeftWinger(lineUp.getLeftWinger().getName());
        dto.setRightWinger(lineUp.getRightWinger().getName());
        dto.setDefender(lineUp.getDefender().getName());
        dto.setGoalkeeper(lineUp.getGoalkeeper().getName());
        return dto;
    }

    public String getRefereeWithMostMatches() throws RefereeNotFoundException {
        List<Referee> referees = matchRepository.findRefereesByMatchCountDesc();

        if (referees.isEmpty()) {
            throw new RefereeNotFoundException("No referees found in any matches");
        }

        Referee topReferee = referees.get(0);

        long matchCount = matchRepository.countByRef(topReferee);

        return "The referee with most matches officiated is " + topReferee.getName() +
               ", overseeing a total of " + matchCount + " matches!";
    }

    public Integer getNumberOfGamesRefereedByReferee(Long refId) {
        return matchRepository.countMatchesByReferee(refId);
    }

    public Integer getNumberOfCardsIssuedByReferee(Long refId) {
        List<Match> matchesRefereed = matchRepository.findMatchesOfReferee(refId);
        int cardsCounter = 0;
        for (Match match : matchesRefereed) {
            cardsCounter += match.getStats().getYellowCards().size();
            cardsCounter += match.getStats().getRedCards().size();
        }
        return cardsCounter;
    }


    public MatchDto mapToDto(Match match) {
        MatchDto dto = new MatchDto();
        dto.setId(match.getId());
        dto.setLineUp1Id(mapLineUpToDto(match.getLineUp1()));
        dto.setLineUp2Id(mapLineUpToDto(match.getLineUp2()));
        dto.setRefId(match.getRef().getId());
        dto.setPlaceId(match.getPlace().getId());
        if (match.getStats() != null) {
            dto.setStatsId(match.getStats().getId());
        }
        if (match.getTournament() != null) {
            dto.setTournamentId(match.getTournament().getId());;
        }
        return dto;
    }

    @Transactional
    public MatchDto createMatch(MatchDto matchDto) {
        Place place = placeRepository.findById(matchDto.getPlaceId())
            .orElseThrow(() -> new NoSuchElementException("Place not found."));

        if (!matchRepository.findByPlace(place).isEmpty()) {
            throw new IllegalArgumentException("This place is already booked for another match.");
        }

        if (place.getDateTime() != null) {
            LocalDateTime matchTime = place.getDateTime();
            LocalDateTime startWindow = matchTime.minusMinutes(150);
            LocalDateTime endWindow = matchTime.plusMinutes(150);

            if (matchRepository.existsByPlaceAndDateTimeBetween(place, startWindow, endWindow)) {
                throw new IllegalArgumentException("Time conflict: Place already booked within 2h30m window.");
            }
        }

        Referee ref = refereeRepository.findById(matchDto.getRefId())
            .orElseThrow(() -> new NoSuchElementException("Referee not found."));

        Match match = new Match();
        match.setRef(ref);
        match.setPlace(place);
        match = matchRepository.save(match);

        MatchStats matchStats = matchStatsHandler.createMatchStats(match);
        match.setStats(matchStats);

        LineUp lineUp1 = lineUpBuilder.buildLineUp(matchDto.getLineUp1Id(), match);
        if (!lineUp1.lineUpIsComplete()) throw new IllegalArgumentException("LineUp 1 is incomplete.");

        LineUp lineUp2 = lineUpBuilder.buildLineUp(matchDto.getLineUp2Id(), match);
        if (!lineUp2.lineUpIsComplete()) throw new IllegalArgumentException("LineUp 2 is incomplete.");

        lineUpRepository.save(lineUp1);
        lineUpRepository.save(lineUp2);

        match.setLineUp1(lineUp1);
        match.setLineUp2(lineUp2);

        updateTeamHistories(lineUp1.getTeam(), match);
        updateTeamHistories(lineUp2.getTeam(), match);

        Match savedMatch = matchRepository.save(match);

        return mapToDto(savedMatch);
    }

    @Transactional
    public MatchDto createSimpleMatch(SimpleMatchDto simpleMatchDto) {
        // Validate all required IDs are present
        if ((Long) simpleMatchDto.getPlaceId() == null || (Long) simpleMatchDto.getRefereeId() == null || 
            (Long)  simpleMatchDto.getLineUp1Id() == null || (Long) simpleMatchDto.getLineUp2Id() == null) {
            throw new IllegalArgumentException("All IDs (place, referee, lineUp1, lineUp2) must be provided");
        }

        // Fetch and validate place
        Place place = placeRepository.findById(simpleMatchDto.getPlaceId())
            .orElseThrow(() -> new NoSuchElementException("Place not found with ID: " + simpleMatchDto.getPlaceId()));

        // Check place availability
        if (!matchRepository.findByPlace(place).isEmpty()) {
            throw new IllegalArgumentException("This place is already booked for another match.");
        }

        if (place.getDateTime() != null) {
            LocalDateTime matchTime = place.getDateTime();
            LocalDateTime startWindow = matchTime.minusMinutes(150);
            LocalDateTime endWindow = matchTime.plusMinutes(150);

            if (matchRepository.existsByPlaceAndDateTimeBetween(place, startWindow, endWindow)) {
                throw new IllegalArgumentException("Time conflict: Place already booked within 2h30m window.");
            }
        }

        // Fetch and validate referee
        Referee ref = refereeRepository.findById(simpleMatchDto.getRefereeId())
            .orElseThrow(() -> new NoSuchElementException("Referee not found with ID: " + simpleMatchDto.getRefereeId()));

            // Check referee availability within 2h30m window
            if (place.getDateTime() != null) {
                LocalDateTime matchTime = place.getDateTime();
                LocalDateTime startWindow = matchTime.minusMinutes(150);
                LocalDateTime endWindow = matchTime.plusMinutes(150);
                
                if (matchRepository.existsByRefAndDateTimeBetween(ref, startWindow, endWindow)) {
                    throw new IllegalArgumentException("Referee is already assigned to another match within 2h30m of this time.");
                }
        }
        // Fetch and validate lineups
        LineUp lineUp1 = lineUpRepository.findById(simpleMatchDto.getLineUp1Id())
            .orElseThrow(() -> new NoSuchElementException("LineUp 1 not found with ID: " + simpleMatchDto.getLineUp1Id()));
        
        LineUp lineUp2 = lineUpRepository.findById(simpleMatchDto.getLineUp2Id())
            .orElseThrow(() -> new NoSuchElementException("LineUp 2 not found with ID: " + simpleMatchDto.getLineUp2Id()));

        // Validate lineups are complete
        if (!lineUp1.lineUpIsComplete()) {
            throw new IllegalArgumentException("LineUp 1 is incomplete");
        }
        if (!lineUp2.lineUpIsComplete()) {
            throw new IllegalArgumentException("LineUp 2 is incomplete");
        }

        // Validate teams exist for both lineups
        if (lineUp1.getTeam() == null || lineUp2.getTeam() == null) {
            throw new IllegalArgumentException("Both lineups must have valid teams");
        }

        // Validate teams are different
        if (((Long) lineUp1.getTeam().getId()).equals(lineUp2.getTeam().getId())) {
            throw new IllegalArgumentException("A team cannot play against itself");
        }

        if (place.getDateTime() != null) {
        LocalDateTime matchTime = place.getDateTime();
        LocalDateTime startWindow = matchTime.minusMinutes(150);
        LocalDateTime endWindow = matchTime.plusMinutes(150);
        
        // Combine players from both lineups
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(lineUp1.getPlayers());
        allPlayers.addAll(lineUp2.getPlayers());
        
        validatePlayerAvailability(allPlayers, startWindow, endWindow);
    }

        // Create the match
        Match match = new Match();
        match.setRef(ref);
        match.setPlace(place);
        match = matchRepository.save(match);

        // Create match stats
        MatchStats matchStats = matchStatsHandler.createMatchStats(match);
        match.setStats(matchStats);

        // Set lineups
        lineUp1.setMatch(match);
        lineUp2.setMatch(match);
        lineUpRepository.save(lineUp1);
        lineUpRepository.save(lineUp2);

        match.setLineUp1(lineUp1);
        match.setLineUp2(lineUp2);

        // Update team histories
        updateTeamHistories(lineUp1.getTeam(), match);
        updateTeamHistories(lineUp2.getTeam(), match);

        Match savedMatch = matchRepository.save(match);

        return mapToDto(savedMatch);
    }

    private void updateTeamHistories(Team team, Match match) {
        if (team.getHistory() == null) {
            team.setHistory(new History());
        }
        team.getHistory().addPlayedMatch(match.getStats());
        teamRepository.save(team);
    }

    private void validatePlayerAvailability(List<Player> players, LocalDateTime start, LocalDateTime end) {
        List<String> busyPlayers = players.stream()
            .filter(player -> playerRepository.existsByPlayerAndDateTimeBetween(player, start, end))
            .map(Player::getName)
            .collect(Collectors.toList());
        
        if (!busyPlayers.isEmpty()) {
            throw new IllegalArgumentException(
                "The following players are already scheduled in another match within 2h30m: " + 
                String.join(", ", busyPlayers));
        }
    }


    public MatchDto registerAllMatchStats(long matchId, MatchStats stats) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new NoSuchElementException("Match with ID " + matchId + " not found."));

        match.setStats(stats);
        Match savedMatch = matchRepository.save(match);
        return mapToDto(savedMatch);
    }

    public MatchDto registerPlayerGoals(long matchId, long playerId, int goals) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        matchStatsHandler.recordPlayerGoals(match, player, goals);
        return mapToDto(matchRepository.save(match));
    }

    @Transactional
    public MatchDto registerScore(long matchId, Map<Long, Integer> playerGoals) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new NoSuchElementException("Match not found"));

        playerGoals.keySet().forEach(playerId -> {
            if (!playerRepository.existsById(playerId)) {
                throw new NoSuchElementException("Player not found: " + playerId);
            }
        });

        playerGoals.forEach((playerId, goals) -> {
            Player player = playerRepository.getReferenceById(playerId);
            matchStatsHandler.recordPlayerGoals(match, player, goals);
        });

        return mapToDto(matchRepository.save(match));
    }

    public List<MatchDto> searchWithFilters(String matchPlanning, Integer matchGoals, String matchLocation, String matchTime) {
        List<Match> matches;
        if (matchLocation == null || matchLocation.equals(""))
            matches = matchRepository.findAll();
        else
            matches = matchRepository.findMatchesByStadium(matchLocation);
        List<MatchDto> matchingLocationMatches = matches.stream().map(this::mapToDto).toList();
        List<MatchDto> matchingMatches = new LinkedList<>();

        for (MatchDto match : matchingLocationMatches) {
            if (!matchPlanning.equals("") && !matchPlanning.equals(getMatchPlanning(match)))
                continue;
            if (matchGoals != null && matchGoals != getMatchGoals(match))
                continue;
            if (!matchTime.equals("") && !matchTime.equals(getMatchTimeOfDay(match)))
                continue;
            matchingMatches.add(match);
        }

        return matchingMatches;
    }

    public String getMatchPlanning(MatchDto match) {
        Match matchEntity = matchRepository.findById(match.getId()).get();
        Optional<Tournament> optionalTournament = tournamentRepository.findById(match.getTournamentId());
        if (optionalTournament.isPresent()) {
            Tournament tournament = optionalTournament.get();
            if (tournament.getPlayedMatches().contains(matchEntity))
                return "played";
        }
        return "to play";
    }

    public int getMatchGoals(MatchDto match) {
        MatchStats stats = matchStatsRepository.findById(match.getId()).get();
        return stats.getTeam1Score() + stats.getTeam2Score();
    }

    public String getMatchTimeOfDay(MatchDto match) {
        Place matchPlace = placeRepository.findById(match.getId()).get();
        int matchHour = matchPlace.getDateTime().getHour();

        if (matchHour >= 5 && matchHour < 12)
            return "morning";
        else if (matchHour >= 12 && matchHour < 21)
            return "afternoon";
        else 
            return "night";
    }

    public String getMatchLocation(MatchDto match) {
        Place matchPlace = placeRepository.findById(match.getId()).get();
        return matchPlace.getStadium();
    }

    public Integer countMatchesByPlayer(Long playerId) {
        return matchRepository.countMatchesByPlayer(playerId);
    }

    @Transactional
    public boolean deleteMatch(long matchId) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isEmpty()) {
            return false;
        }
        Match match = matchOptional.get();
        if (match.getLineUp1() != null && match.getLineUp1().getTeam() != null) {
            Team team1 = match.getLineUp1().getTeam();
            if (team1.getHistory() != null && match.getStats() != null) {
                team1.getHistory().removePlayedMatch(match.getStats());
                teamRepository.save(team1);
            }
        }
        
        if (match.getLineUp2() != null && match.getLineUp2().getTeam() != null) {
            Team team2 = match.getLineUp2().getTeam();
            if (team2.getHistory() != null && match.getStats() != null) {
                team2.getHistory().removePlayedMatch(match.getStats());
                teamRepository.save(team2);
            }
        }
        
        if (match.getLineUp1() != null) {
            LineUp lineUp1 = match.getLineUp1();
            lineUp1.setMatch(null);
            lineUpRepository.save(lineUp1);
        }
        
        if (match.getLineUp2() != null) {
            LineUp lineUp2 = match.getLineUp2();
            lineUp2.setMatch(null);
            lineUpRepository.save(lineUp2);
        }

        MatchStats matchStats = match.getStats();
        match.setLineUp1(null);
        match.setLineUp2(null);
        match.setStats(null);
        match.setRef(null);
        match.setPlace(null);
        match.setTournament(null);     
        matchRepository.save(match);
        
        if (matchStats != null) {
            matchStatsRepository.delete(matchStats);
        }
        matchRepository.delete(match);      
        return true;
    }

}
