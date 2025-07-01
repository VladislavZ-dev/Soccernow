package pt.ul.fc.css.soccernow.handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ul.fc.css.soccernow.dto.HistoryDto;
import pt.ul.fc.css.soccernow.entities.History;
import pt.ul.fc.css.soccernow.entities.MatchStats;
import pt.ul.fc.css.soccernow.entities.Tournament;
import pt.ul.fc.css.soccernow.entities.Placement;
import pt.ul.fc.css.soccernow.repositories.HistoryRepository;
import pt.ul.fc.css.soccernow.repositories.TournamentRepository;

@Service
public class HistoryHandler {

    @Autowired
    private HistoryRepository historyRepository;
    
    @Autowired
    private TournamentRepository tournamentRepository;

    public HistoryDto findHistory(long id) {
        History searchedHistory = historyRepository.findById(id).get();
        return mapToDto(searchedHistory);
    }

    public History findHistoryEntity(long id) {
        History searchedHistory = historyRepository.findById(id).get();
        return searchedHistory;
    }

    public History createHistory() {
        return new History();
    }

    public HistoryDto mapToDto(History history) {
        HistoryDto newHistoryDto = new HistoryDto(history.getId(), history.getTeam().getName(), getPlayedMatchesIds(history.getPlayedMatches()));
        return newHistoryDto;
    }

    private List<Long> getPlayedMatchesIds(List<MatchStats> playedMatches) {
        List<Long> playedMatchesIds = new LinkedList<>();
        for (MatchStats match : playedMatches)
            playedMatchesIds.add(match.getId());
        return playedMatchesIds;
    }

    /**
     * Update tournament placement using tournament name
     */
    public History updateTournamentPlacement(History history, String tournamentName, String placement) {
        // Find the tournament by name
        Optional<Tournament> tournamentOpt = tournamentRepository.findByExactName(tournamentName)
                .stream().findFirst();
        
        if (tournamentOpt.isPresent()) {
            Tournament tournament = tournamentOpt.get();
            Placement placementEnum = Placement.fromString(placement);
            history.getTournamentPlacements().put(tournament, placementEnum);
            return historyRepository.save(history);
        } else {
            throw new IllegalArgumentException("Tournament not found with name: " + tournamentName);
        }
    }
    
    /**
     * Update tournament placement using tournament entity directly
     */
    public History updateTournamentPlacement(History history, Tournament tournament, Placement placement) {
        history.getTournamentPlacements().put(tournament, placement);
        return historyRepository.save(history);
    }
    
    /**
     * Add initial tournament placement for a team when tournament is created
     */
    public History addInitialTournamentPlacement(History history, Tournament tournament) {
        history.getTournamentPlacements().put(tournament, Placement.PARTICIPANT);
        return historyRepository.save(history);
    }
}