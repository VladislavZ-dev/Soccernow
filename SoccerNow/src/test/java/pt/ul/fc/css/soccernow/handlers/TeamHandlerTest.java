package pt.ul.fc.css.soccernow.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.entities.*;
import pt.ul.fc.css.soccernow.exceptions.*;
import pt.ul.fc.css.soccernow.repositories.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamHandlerTest {

    @Mock private TeamRepository teamRepository;
    @Mock private PlayerRepository playerRepository;
    @Mock private PlayerHandler playerHandler;
    @Mock private HistoryHandler historyHandler;
    @Mock private MatchRepository matchRepository;
    @Mock private LineUpRepository lineUpRepository;

    @InjectMocks
    private TeamHandler teamHandler;

    private Team testTeam;
    private TeamDto testTeamDto;
    private Player testPlayer;
    private History testHistory;

    @BeforeEach
    void setUp() {
        testHistory = new History();
        testHistory.setId(1L);

        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("Test Player");

        testTeam = new Team("Test Team");
        testTeam.setId(1L);
        testTeam.setHistory(testHistory);
        testTeam.setPlayers(new ArrayList<>(List.of(testPlayer)));

        testTeamDto = new TeamDto(1L, "Test Team", 1L, List.of("Test Player"));
    }

    @Test
    void getAllTeams_ShouldReturnAllTeams() {
        when(teamRepository.findAll()).thenReturn(List.of(testTeam));

        List<TeamDto> result = teamHandler.getAllTeams();

        assertEquals(1, result.size());
        assertEquals("Test Team", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getHistoryId());
        assertEquals(1, result.get(0).getPlayers().size());
        verify(teamRepository).findAll();
    }

    @Test
    void createNewTeam_ShouldCreateTeamSuccessfully() {
        TeamDto newTeamDto = new TeamDto();
        newTeamDto.setName("New Team");

        History newHistory = new History();
        newHistory.setId(2L);

        when(teamRepository.findByName("New Team")).thenReturn(Collections.emptyList());
        when(historyHandler.createHistory()).thenReturn(newHistory);
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
            Team t = invocation.getArgument(0);
            t.setId(2L);
            t.setHistory(newHistory);
            return t;
        });

        TeamDto result = teamHandler.createNewTeam(newTeamDto);

        assertNotNull(result);
        assertEquals("New Team", result.getName());
        assertEquals(2L, result.getId());
        assertEquals(2L, result.getHistoryId());
        assertNull(result.getPlayers());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void createNewTeam_ShouldReturnNullForDuplicateTeam() {
        TeamDto existingTeamDto = new TeamDto();
        existingTeamDto.setName("Existing Team");

        when(teamRepository.findByName("Existing Team")).thenReturn(List.of(testTeam));

        TeamDto result = teamHandler.createNewTeam(existingTeamDto);

        assertNull(result);
        verify(teamRepository, never()).save(any());
    }

    @Test
    void createNewTeam_ShouldReturnNullForNullName() {
        TeamDto nullNameDto = new TeamDto();
        nullNameDto.setName(null);

        TeamDto result = teamHandler.createNewTeam(nullNameDto);

        assertNull(result);
        verify(teamRepository, never()).save(any());
    }

    @Test
    void removeTeam_ShouldDeleteTeamSuccessfully() {
        LineUp testLineUp = new LineUp();
        testLineUp.setTeam(testTeam);

        when(teamRepository.findByName("Test Team")).thenReturn(List.of(testTeam));
        when(matchRepository.findFutureMatches()).thenReturn(Collections.emptyList());
        when(lineUpRepository.findLineUpsByTeamName("Test Team")).thenReturn(List.of(testLineUp));
        when(lineUpRepository.save(any(LineUp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        teamHandler.removeTeam("Test Team");

        verify(teamRepository).deleteByName("Test Team");
        verify(playerHandler).removePlayersFromTeam("Test Team");
        verify(lineUpRepository).save(testLineUp);
        assertNull(testLineUp.getTeam());
    }

    @Test
    void removeTeam_ShouldNotDeleteTeamWithFutureMatches() {
        Match futureMatch = new Match();
        futureMatch.setPlace(new Place());
        futureMatch.getPlace().setDateTime(LocalDateTime.now().plusDays(1));
        futureMatch.setLineUp1(new LineUp());
        futureMatch.getLineUp1().setTeam(testTeam);

        when(teamRepository.findByName("Test Team")).thenReturn(List.of(testTeam));
        when(matchRepository.findFutureMatches()).thenReturn(List.of(futureMatch));

        teamHandler.removeTeam("Test Team");

        verify(teamRepository, never()).deleteByName(anyString());
    }

    @Test
    void updateTeam_ShouldUpdateTeamNameAndPlayers() {
        TeamDto updateDto = new TeamDto();
        updateDto.setName("Updated Team");
        updateDto.setPlayers(List.of("Test Player"));

        when(teamRepository.findByName("Old Team")).thenReturn(List.of(testTeam));
        when(playerRepository.findByName("Test Player")).thenReturn(Optional.of(testPlayer));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        TeamDto result = teamHandler.updateTeam(updateDto, "Old Team");

        assertNotNull(result);
        assertEquals("Updated Team", result.getName());
        assertEquals(1, result.getPlayers().size());
        assertEquals("Test Player", result.getPlayers().get(0));
    }

    @Test
    void updateTeam_ShouldHandleNullPlayerList() {
        TeamDto updateDto = new TeamDto();
        updateDto.setName("Updated Team");
        updateDto.setPlayers(null);

        when(teamRepository.findByName("Old Team")).thenReturn(List.of(testTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(testTeam);

        TeamDto result = teamHandler.updateTeam(updateDto, "Old Team");

        assertNotNull(result);
        assertEquals("Updated Team", result.getName());
        assertNull(result.getPlayers());
    }

    @Test
    void searchTeam_ShouldReturnTeamDto() {
        when(teamRepository.findByName("Test Team")).thenReturn(List.of(testTeam));

        TeamDto result = teamHandler.searchTeam("Test Team");

        assertNotNull(result);
        assertEquals("Test Team", result.getName());
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getHistoryId());
        assertEquals(1, result.getPlayers().size());
    }

    @Test
    void searchTeam_ShouldReturnNullForEmptyName() {
        TeamDto result = teamHandler.searchTeam("");

        assertNull(result);
        verify(teamRepository, never()).findByName(anyString());
    }

    @Test
    void searchTeamEntity_ShouldReturnTeam() {
        when(teamRepository.findByName("Test Team")).thenReturn(List.of(testTeam));

        Team result = teamHandler.searchTeamEntity("Test Team");

        assertNotNull(result);
        assertEquals("Test Team", result.getName());
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getHistory().getId());
        assertEquals(1, result.getPlayers().size());
    }

    @Test
    void getNumberOfPlayersOfTeam_ShouldReturnCorrectCount() {
        when(teamRepository.findByName("Test Team")).thenReturn(List.of(testTeam));

        int count = teamHandler.getNumberOfPlayersOfTeam("Test Team");

        assertEquals(1, count);
    }

    @Test
    void getNumberOfPlayersOfTeam_ShouldReturnZeroForNoPlayers() {
        testTeam.setPlayers(Collections.emptyList());
        when(teamRepository.findByName("Test Team")).thenReturn(List.of(testTeam));

        int count = teamHandler.getNumberOfPlayersOfTeam("Test Team");

        assertEquals(0, count);
    }
}
