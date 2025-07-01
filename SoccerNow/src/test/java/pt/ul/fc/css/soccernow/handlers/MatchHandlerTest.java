package pt.ul.fc.css.soccernow.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.ul.fc.css.soccernow.builders.LineUpBuilder;
import pt.ul.fc.css.soccernow.dto.LineUpDto;
import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.entities.*;
import pt.ul.fc.css.soccernow.exceptions.RefereeNotFoundException;
import pt.ul.fc.css.soccernow.repositories.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchHandlerTest {

    @Mock private MatchRepository matchRepository;
    @Mock private RefereeRepository refereeRepository;
    @Mock private PlaceRepository placeRepository;
    @Mock private LineUpBuilder lineUpBuilder;
    @Mock private LineUpRepository lineUpRepository;
    @Mock private MatchStatsHandler matchStatsHandler;
    @Mock private PlayerRepository playerRepository;
    @Mock private TeamRepository teamRepository;

    @InjectMocks
    private MatchHandler matchHandler;

    private Match testMatch;
    private MatchDto testMatchDto;
    private LineUpDto testLineUpDto;
    private Place testPlace;
    private Referee testReferee;

    @BeforeEach
    void setUp() {
        testReferee = new Referee();
        testReferee.setId(1L);
        testReferee.setName("Test Referee");

        testPlace = new Place();
        testPlace.setId(1L);
        testPlace.setStadium("Test Stadium");
        testPlace.setDateTime(LocalDateTime.now().plusHours(2));

        Team team1 = new Team();
        team1.setId(1L);
        team1.setName("Team A");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setName("Team B");

        Player testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("Test Player");

        LineUp lineUp1 = new LineUp();
        lineUp1.setTeam(team1);
        lineUp1.setCaptain(testPlayer);
        lineUp1.setPivot(testPlayer);
        lineUp1.setLeftWinger(testPlayer);
        lineUp1.setRightWinger(testPlayer);
        lineUp1.setDefender(testPlayer);
        lineUp1.setGoalkeeper(testPlayer);

        LineUp lineUp2 = new LineUp();
        lineUp2.setTeam(team2);
        lineUp2.setCaptain(testPlayer);
        lineUp2.setPivot(testPlayer);
        lineUp2.setLeftWinger(testPlayer);
        lineUp2.setRightWinger(testPlayer);
        lineUp2.setDefender(testPlayer);
        lineUp2.setGoalkeeper(testPlayer);

        testMatch = new Match();
        testMatch.setId(1L);
        testMatch.setRef(testReferee);
        testMatch.setPlace(testPlace);
        testMatch.setLineUp1(lineUp1);
        testMatch.setLineUp2(lineUp2);

        MatchStats stats = new MatchStats();
        stats.setId(1L);
        testMatch.setStats(stats);

        testLineUpDto = new LineUpDto();
        testLineUpDto.setTeam("Team A");
        testLineUpDto.setCaptain("Test Player");
        testLineUpDto.setPivot("Test Player");
        testLineUpDto.setLeftWinger("Test Player");
        testLineUpDto.setRightWinger("Test Player");
        testLineUpDto.setDefender("Test Player");
        testLineUpDto.setGoalkeeper("Test Player");

        testMatchDto = new MatchDto();
        testMatchDto.setId(1L);
        testMatchDto.setRefId(1L);
        testMatchDto.setPlaceId(1L);
        testMatchDto.setStatsId(1L);
        testMatchDto.setLineUp1Id(testLineUpDto);
        testMatchDto.setLineUp2Id(testLineUpDto);
    }

    @Test
    void getAllMatches_ShouldReturnAllMatches() {
        when(matchRepository.findAll()).thenReturn(List.of(testMatch));

        List<MatchDto> result = matchHandler.getAllMatches();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(matchRepository).findAll();
    }

    @Test
    void getMatchById_ShouldReturnMatch() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));

        MatchDto result = matchHandler.getMatchById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getMatchById_ShouldReturnNullWhenNotFound() {
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());

        MatchDto result = matchHandler.getMatchById(1L);

        assertNull(result);
    }

    @Test
    void getMatchByIdEntity_ShouldReturnMatch() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));

        Match result = matchHandler.getMatchByIdEntity(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getRefereeWithMostMatches_ShouldReturnTopReferee() throws RefereeNotFoundException {
        when(matchRepository.findRefereesByMatchCountDesc()).thenReturn(List.of(testReferee));
        when(matchRepository.countByRef(testReferee)).thenReturn(5L);

        String result = matchHandler.getRefereeWithMostMatches();

        assertTrue(result.contains("Test Referee"));
        assertTrue(result.contains("5"));
    }

    @Test
    void getRefereeWithMostMatches_ShouldThrowWhenNoReferees() {
        when(matchRepository.findRefereesByMatchCountDesc()).thenReturn(Collections.emptyList());

        assertThrows(RefereeNotFoundException.class, () -> matchHandler.getRefereeWithMostMatches());
    }

    @Test
    void createMatch_ShouldCreateNewMatch() {

        History team1History = new History();
        team1History.setPlayedMatches(new ArrayList<>());
        testMatch.getLineUp1().getTeam().setHistory(team1History);

        History team2History = new History();
        team2History.setPlayedMatches(new ArrayList<>());
        testMatch.getLineUp2().getTeam().setHistory(team2History);

        when(placeRepository.findById(1L)).thenReturn(Optional.of(testPlace));
        when(refereeRepository.findById(1L)).thenReturn(Optional.of(testReferee));
        when(matchRepository.findByPlace(testPlace)).thenReturn(Collections.emptyList());
        when(matchRepository.existsByPlaceAndDateTimeBetween(any(), any(), any())).thenReturn(false);
        when(lineUpBuilder.buildLineUp(any(), any())).thenReturn(testMatch.getLineUp1(), testMatch.getLineUp2());
        when(matchRepository.save(any())).thenReturn(testMatch);
        when(matchStatsHandler.createMatchStats(any())).thenReturn(new MatchStats());
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MatchDto result = matchHandler.createMatch(testMatchDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(matchRepository, times(2)).save(any());
        verify(teamRepository, times(2)).save(any(Team.class));
    }

    @Test
    void createMatch_ShouldThrowWhenPlaceBooked() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(testPlace));
        when(matchRepository.findByPlace(testPlace)).thenReturn(List.of(new Match()));

        assertThrows(IllegalArgumentException.class, () -> matchHandler.createMatch(testMatchDto));
    }

    @Test
    void createMatch_ShouldThrowWhenTimeConflict() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(testPlace));
        when(matchRepository.findByPlace(testPlace)).thenReturn(Collections.emptyList());
        when(matchRepository.existsByPlaceAndDateTimeBetween(any(), any(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> matchHandler.createMatch(testMatchDto));
    }

    @Test
    void registerAllMatchStats_ShouldUpdateStats() {
        MatchStats newStats = new MatchStats();
        newStats.setId(2L);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));
        when(matchRepository.save(any())).thenReturn(testMatch);

        MatchDto result = matchHandler.registerAllMatchStats(1L, newStats);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(matchRepository).save(testMatch);
    }

    @Test
    void registerPlayerGoals_ShouldUpdatePlayerGoals() {
        Player player = new Player();
        player.setId(1L);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player));
        when(matchRepository.save(any())).thenReturn(testMatch);

        MatchDto result = matchHandler.registerPlayerGoals(1L, 1L, 2);

        assertNotNull(result);
        verify(matchStatsHandler).recordPlayerGoals(testMatch, player, 2);
    }

    @Test
    void registerScore_ShouldUpdateMultiplePlayerGoals() {
        Player player1 = new Player();
        player1.setId(1L);
        Player player2 = new Player();
        player2.setId(2L);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(testMatch));
        when(playerRepository.existsById(1L)).thenReturn(true);
        when(playerRepository.existsById(2L)).thenReturn(true);
        when(playerRepository.getReferenceById(1L)).thenReturn(player1);
        when(playerRepository.getReferenceById(2L)).thenReturn(player2);
        when(matchRepository.save(any())).thenReturn(testMatch);

        Map<Long, Integer> goals = Map.of(1L, 1, 2L, 2);
        MatchDto result = matchHandler.registerScore(1L, goals);

        assertNotNull(result);
        verify(matchStatsHandler, times(2)).recordPlayerGoals(any(), any(), anyInt());
    }

    @Test
    void mapToDto_ShouldMapCorrectly() {
        MatchDto result = matchHandler.mapToDto(testMatch);

        assertEquals(1L, result.getId());
        assertEquals(1L, result.getRefId());
        assertEquals(1L, result.getPlaceId());
        assertEquals(1L, result.getStatsId());
        assertNotNull(result.getLineUp1Id());
        assertNotNull(result.getLineUp2Id());
    }
}
