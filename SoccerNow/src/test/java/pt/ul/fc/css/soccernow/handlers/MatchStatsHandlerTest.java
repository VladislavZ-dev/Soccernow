package pt.ul.fc.css.soccernow.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.dto.MatchStatsDto;
import pt.ul.fc.css.soccernow.entities.*;
import pt.ul.fc.css.soccernow.exceptions.PlayerNotFoundException;
import pt.ul.fc.css.soccernow.repositories.MatchStatsRepository;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchStatsHandlerTest {

    @Mock
    private MatchStatsRepository matchStatsRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private MatchStatsHandler matchStatsHandler;

    private Match testMatch;
    private MatchStats testStats;
    private Player testPlayer;
    private Team testTeam1;
    private Team testTeam2;
    private LineUp testLineUp1;
    private LineUp testLineUp2;

    @BeforeEach
    void setUp() {
        // Setup test player
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("Test Player");

        // Setup test teams
        testTeam1 = new Team();
        testTeam1.setId(1L);
        testTeam1.setName("Team A");

        testTeam2 = new Team();
        testTeam2.setId(2L);
        testTeam2.setName("Team B");

        // Setup lineups
        testLineUp1 = new LineUp();
        testLineUp1.setTeam(testTeam1);
        testLineUp1.setCaptain(testPlayer);
        testLineUp1.setPivot(testPlayer);
        testLineUp1.setLeftWinger(testPlayer);
        testLineUp1.setRightWinger(testPlayer);
        testLineUp1.setDefender(testPlayer);
        testLineUp1.setGoalkeeper(testPlayer);

        testLineUp2 = new LineUp();
        testLineUp2.setTeam(testTeam2);
        testLineUp2.setCaptain(testPlayer);
        testLineUp2.setPivot(testPlayer);
        testLineUp2.setLeftWinger(testPlayer);
        testLineUp2.setRightWinger(testPlayer);
        testLineUp2.setDefender(testPlayer);
        testLineUp2.setGoalkeeper(testPlayer);

        // Setup match
        testMatch = new Match();
        testMatch.setId(1L);
        testMatch.setLineUp1(testLineUp1);
        testMatch.setLineUp2(testLineUp2);

        // Setup match stats
        testStats = new MatchStats();
        testStats.setId(1L);
        testStats.setMatch(testMatch);
        testStats.setTeam1Score(2);
        testStats.setTeam2Score(1);
        testStats.setWinner("Team A");
        testStats.getYellowCards().add(testPlayer);
        testStats.getRedCards().add(testPlayer);
        testStats.getPlayerGoals().put(testPlayer, 1);

        testMatch.setStats(testStats);
    }

    @Test
    void findMatchStats_ShouldReturnStatsDto() {
        MatchStats freshStats = new MatchStats();
        freshStats.setId(1L);
        freshStats.setMatch(testMatch);
        freshStats.setTeam1Score(0);
        freshStats.setTeam2Score(0);
        freshStats.setWinner(null);

        when(matchStatsRepository.findById(1L)).thenReturn(Optional.of(freshStats));

        MatchStatsDto result = matchStatsHandler.findMatchStats(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(0, result.getTeam1Score());
        assertEquals(0, result.getTeam2Score());
        assertNull(result.getWinner());
        assertTrue(result.getYellowCards().isEmpty());
        assertTrue(result.getRedCards().isEmpty());
    }

    @Test
    void findMatchStats_ShouldThrowWhenNotFound() {
        when(matchStatsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            matchStatsHandler.findMatchStats(1L));
    }

    @Test
    void findMatchStatsEntity_ShouldReturnStats() {
        when(matchStatsRepository.findById(1L)).thenReturn(Optional.of(testStats));

        MatchStats result = matchStatsHandler.findMatchStatsEntity(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testMatch, result.getMatch());
    }

    @Test
    void createMatchStats_ShouldCreateAndSaveNewStats() {
        Match newMatch = new Match();
        newMatch.setId(2L);

        when(matchStatsRepository.save(any(MatchStats.class))).thenAnswer(invocation -> {
            MatchStats stats = invocation.getArgument(0);
            stats.setId(2L);
            return stats;
        });

        MatchStats result = matchStatsHandler.createMatchStats(newMatch);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(newMatch, result.getMatch());
        verify(matchStatsRepository).save(any(MatchStats.class));
    }

    @Test
    void recordPlayerGoals_ShouldUpdateExistingStats() {
        testStats.getPlayerGoals().clear();
        testStats.getPlayerGoals().put(testPlayer, 1);
        testStats.setTeam1Score(1);
        testStats.setTeam2Score(0);

        when(matchStatsRepository.save(any(MatchStats.class))).thenReturn(testStats);

        matchStatsHandler.recordPlayerGoals(testMatch, testPlayer, 1);

        assertEquals(2, testStats.getPlayerGoals().get(testPlayer));
        assertEquals(2, testStats.getTeam1Score());
        verify(matchStatsRepository).save(testStats);
    }

    @Test
    void getPlayerGoalsInMatch_ShouldReturnZeroWhenNoStats() {
        Match matchWithoutStats = new Match();
        matchWithoutStats.setId(4L);

        int goals = matchStatsHandler.getPlayerGoalsInMatch(matchWithoutStats, testPlayer);

        assertEquals(0, goals);
    }

    @Test
    void getPlayerGoalsInMatch_ShouldReturnCorrectCount() {
        int goals = matchStatsHandler.getPlayerGoalsInMatch(testMatch, testPlayer);

        assertEquals(1, goals);
    }

    @Test
    void getAverageGoalsPerGameForPlayer_ShouldCalculateCorrectly() throws PlayerNotFoundException {
        when(playerRepository.findByName("Test Player")).thenReturn(Optional.of(testPlayer));
        when(matchStatsRepository.findMatchesWithPlayerGoals(1L)).thenReturn(List.of(testStats));

        double average = matchStatsHandler.getAverageGoalsPerGameForPlayer("Test Player");

        assertEquals(1.0, average, 0.001);
    }

    @Test
    void getAverageGoalsPerGameForPlayer_ShouldReturnZeroForNoMatches() throws PlayerNotFoundException {
        when(playerRepository.findByName("Test Player")).thenReturn(Optional.of(testPlayer));
        when(matchStatsRepository.findMatchesWithPlayerGoals(1L)).thenReturn(Collections.emptyList());

        double average = matchStatsHandler.getAverageGoalsPerGameForPlayer("Test Player");

        assertEquals(0.0, average, 0.001);
    }

    @Test
    void getAverageGoalsPerGameForPlayer_ShouldThrowWhenPlayerNotFound() {
        when(playerRepository.findByName("Unknown Player")).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () ->
            matchStatsHandler.getAverageGoalsPerGameForPlayer("Unknown Player"));
    }

    @Test
    void getTeamsWithMostWins_ShouldFormatCorrectly() {
        MatchStats statsWithWin = new MatchStats();
        statsWithWin.setWinner("Team A");

        when(matchStatsRepository.findAllWithWinners()).thenReturn(List.of(statsWithWin));

        String result = matchStatsHandler.getTeamsWithMostWins();

        assertTrue(result.startsWith("Teams with most wins:"));
        assertTrue(result.contains("Team A: 1"));
    }

    @Test
    void registerCard_ShouldAddYellowCard() {
        Match match = new Match();
        match.setId(5L);

        when(matchStatsRepository.save(any(MatchStats.class))).thenAnswer(invocation -> {
            MatchStats stats = invocation.getArgument(0);
            match.setStats(stats);
            return stats;
        });

        String result = matchStatsHandler.registerCard(match, testPlayer, false);

        assertNotNull(match.getStats());
        assertTrue(match.getStats().getYellowCards().contains(testPlayer));
        assertFalse(match.getStats().getRedCards().contains(testPlayer));
        assertTrue(result.contains("successfully registered"));
    }

    @Test
    void registerCard_ShouldAddRedCard() {
        Match match = new Match();
        match.setId(6L);

        when(matchStatsRepository.save(any(MatchStats.class))).thenAnswer(invocation -> {
            MatchStats stats = invocation.getArgument(0);
            match.setStats(stats);
            return stats;
        });

        String result = matchStatsHandler.registerCard(match, testPlayer, true);

        assertNotNull(match.getStats());
        assertTrue(match.getStats().getRedCards().contains(testPlayer));
        assertFalse(match.getStats().getYellowCards().contains(testPlayer));
        assertTrue(result.contains("successfully registered"));
    }

    @Test
    void registerCard_ShouldNotDuplicateCards() {
        when(matchStatsRepository.save(any(MatchStats.class))).thenReturn(testStats);

        // Test player already has a yellow and red card in testStats
        matchStatsHandler.registerCard(testMatch, testPlayer, false);
        matchStatsHandler.registerCard(testMatch, testPlayer, true);

        assertEquals(1, testStats.getYellowCards().size());
        assertEquals(1, testStats.getRedCards().size());
        verify(matchStatsRepository, times(2)).save(testStats);
    }
}
