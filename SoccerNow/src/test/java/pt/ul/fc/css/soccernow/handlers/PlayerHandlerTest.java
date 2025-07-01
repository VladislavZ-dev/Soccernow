package pt.ul.fc.css.soccernow.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Position;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.exceptions.*;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;
import pt.ul.fc.css.soccernow.repositories.TeamRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerHandlerTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private PlayerHandler playerHandler;

    private Player createTestPlayer(String name, Position position) {
        Player player = new Player(name, position);
        player.setId(1L);
        player.setTeams(new ArrayList<>(List.of(createTestTeam())));
        return player;
    }

    private Team createTestTeam() {
        Team team = new Team();
        team.setName("Test Team");
        return team;
    }

    @Test
    void getAllPlayers_ShouldReturnAllPlayers() {
        Player player1 = createTestPlayer("Goalkeeper", Position.GOALKEEPER);
        Player player2 = createTestPlayer("Defender", Position.DEFENDER);

        when(playerRepository.findAll()).thenReturn(List.of(player1, player2));

        List<PlayerDTO> result = playerHandler.getAllPlayers();

        assertEquals(2, result.size());
        assertEquals(Position.GOALKEEPER, result.get(0).getPosition());
        assertEquals(Position.DEFENDER, result.get(1).getPosition());
    }

    @Test
    void updatePlayer_ChangeToRightWinger_ShouldUpdateSuccessfully() throws PlayerNotFoundException, TeamNotFoundException {
        Player existingPlayer = new Player("Existing Player", Position.PIVOT);
        existingPlayer.setId(1L);
        existingPlayer.setTeams(new ArrayList<>(List.of(createTestTeam())));

        PlayerDTO updateDTO = new PlayerDTO();
        updateDTO.setName("Updated Player");
        updateDTO.setPosition(Position.RIGHT_WINGER);
        updateDTO.setTeams(List.of("Test Team"));

        when(playerRepository.findById(1L)).thenReturn(Optional.of(existingPlayer));
        when(teamRepository.findAllByNameIn(anyList())).thenReturn(List.of(createTestTeam()));
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerDTO result = playerHandler.updatePlayer(1L, updateDTO);

        assertEquals(Position.RIGHT_WINGER, result.getPosition());
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void createPlayer_WithGoalkeeperPosition_ShouldCreateSuccessfully() throws PlayerAlreadyExistsException, TeamNotFoundException {
        PlayerDTO newPlayerDTO = new PlayerDTO();
        newPlayerDTO.setName("New Goalie");
        newPlayerDTO.setPosition(Position.GOALKEEPER);
        newPlayerDTO.setTeams(List.of("Test Team"));

        when(playerRepository.findByName("New Goalie")).thenReturn(Optional.empty());
        when(teamRepository.findAllByNameIn(List.of("Test Team"))).thenReturn(List.of(createTestTeam()));
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> {
            Player p = invocation.getArgument(0);
            p.setId(3L);
            return p;
        });

        PlayerDTO result = playerHandler.createPlayer(newPlayerDTO);

        assertEquals(Position.GOALKEEPER, result.getPosition());
    }

    @Test
    void mapToDto_WithDefenderPosition_ShouldMapCorrectly() {
        Player defender = createTestPlayer("Defender", Position.DEFENDER);

        PlayerDTO result = playerHandler.mapToDto(defender);

        assertEquals(Position.DEFENDER, result.getPosition());
        assertEquals(1, result.getTeams().size());
    }

    @Test
    void removePlayersFromTeam_WithPivotPosition_ShouldMaintainPosition() {
        Player pivot = createTestPlayer("Pivot Player", Position.PIVOT);
        pivot.setTeams(new ArrayList<>(List.of(createTestTeam())));

        when(playerRepository.findByTeams_Name("Test Team")).thenReturn(List.of(pivot));
        when(playerRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<PlayerDTO> result = playerHandler.removePlayersFromTeam("Test Team");

        assertEquals(Position.PIVOT, result.get(0).getPosition());
    }

    @Test
    void createPlayer_WithNullPosition_ShouldThrowException() {
        PlayerDTO invalidPlayer = new PlayerDTO();
        invalidPlayer.setName("Invalid Player");
        invalidPlayer.setTeams(List.of("Test Team"));

        assertThrows(IllegalArgumentException.class, () -> playerHandler.createPlayer(invalidPlayer));
    }

    @Test
    void updatePlayer_WithInvalidPositionTransition_ShouldStillUpdate() throws PlayerNotFoundException, TeamNotFoundException {
        Player existingPlayer = new Player("Existing Player", Position.PIVOT);
        existingPlayer.setId(1L);
        existingPlayer.setTeams(new ArrayList<>(List.of(createTestTeam())));

        PlayerDTO updateDTO = new PlayerDTO();
        updateDTO.setName("Updated Player");
        updateDTO.setPosition(Position.GOALKEEPER);
        updateDTO.setTeams(List.of("Test Team"));

        when(playerRepository.findById(1L)).thenReturn(Optional.of(existingPlayer));
        when(teamRepository.findAllByNameIn(anyList())).thenReturn(List.of(createTestTeam()));
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerDTO result = playerHandler.updatePlayer(1L, updateDTO);

        assertEquals(Position.GOALKEEPER, result.getPosition());
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void getPlayerEntityByName_WithRightWinger_ShouldReturnCorrectPlayer() {
        Player winger = createTestPlayer("Right Winger", Position.RIGHT_WINGER);

        when(playerRepository.findByName("Right Winger")).thenReturn(Optional.of(winger));

        Player result = playerHandler.getPlayerEntityByName("Right Winger");

        assertEquals(Position.RIGHT_WINGER, result.getPosition());
    }
}
