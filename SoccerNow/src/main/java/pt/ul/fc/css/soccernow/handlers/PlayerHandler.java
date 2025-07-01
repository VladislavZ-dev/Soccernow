package pt.ul.fc.css.soccernow.handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Position;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;
import pt.ul.fc.css.soccernow.repositories.MatchStatsRepository;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;
import pt.ul.fc.css.soccernow.repositories.TeamRepository;
import pt.ul.fc.css.soccernow.exceptions.PlayerNotFoundException;
import pt.ul.fc.css.soccernow.exceptions.TeamNotFoundException;
import pt.ul.fc.css.soccernow.exceptions.PlayerAlreadyExistsException;

@Service
public class PlayerHandler {
	@Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchStatsHandler matchStatsHandler;

    @Autowired
    private MatchStatsRepository matchStatsRepository;

    @Autowired
    private MatchRepository matchRepository;

    public List<PlayerDTO> getAllPlayers() {
        List<Player> players = playerRepository.findAll();
        return players.stream()
                .map(this::mapToDto)
                .toList();
    }

    public PlayerDTO saveplayer(Player player) {
        return mapToDto(playerRepository.save(player));
    }

    public PlayerDTO getPlayerById(Long id) throws PlayerNotFoundException {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isEmpty()) {
            throw new PlayerNotFoundException(id);
        }
        return mapToDto(playerOptional.get());
    }

    public PlayerDTO getPlayerByName(String name) throws PlayerNotFoundException {
        Optional<Player> playerOptional = playerRepository.findByName(name);
        if (playerOptional.isEmpty()) {
            throw new PlayerNotFoundException(name);
        }
        return mapToDto(playerOptional.get());
    }

    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDto) throws PlayerNotFoundException, TeamNotFoundException {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isEmpty()) {
            throw new PlayerNotFoundException(id);
        }

        Player existingPlayer = playerOptional.get();
        existingPlayer.setName(playerDto.getName());
        existingPlayer.setPosition(playerDto.getPosition());

        if (playerDto.getTeams() != null) {
            List<Team> teams = teamRepository.findAllByNameIn(playerDto.getTeams());
            if (teams.size() != playerDto.getTeams().size()) {
                throw new TeamNotFoundException();
            }
            existingPlayer.setTeams(teams);
        }

        Player updatedPlayer = playerRepository.save(existingPlayer);
        return mapToDto(updatedPlayer);
    }

    public List<PlayerDTO> removePlayersFromTeam(String name) {
        List<Player> playersWithTeam = playerRepository.findByTeams_Name(name);

        for (Player player : playersWithTeam) {
            player.getTeams().removeIf(team -> team.getName().equals(name));
        }

        playerRepository.saveAll(playersWithTeam);

        return playersWithTeam.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public String deletePlayerById(Long id) throws PlayerNotFoundException {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isEmpty()) {
            throw new PlayerNotFoundException(id);
        }

        playerRepository.deleteById(id);
        return "Player with ID " + id + " deleted succesfully";
    }

    @Transactional
    public String deletePlayerByName(String name)  throws PlayerNotFoundException{
        Optional<Player> playerOptional = playerRepository.findByName(name);
        if (playerOptional.isEmpty()) {
            throw new PlayerNotFoundException(name);
        }

        playerRepository.deleteByName(name);
        return "Player with name " + name + " deleted succesfully" ;
    }

    public PlayerDTO mapToDto(Player player) {
        PlayerDTO playerDto = new PlayerDTO();
        playerDto.setId(player.getId());
        playerDto.setName(player.getName());
        playerDto.setPosition(player.getPosition());
        playerDto.setTeams(player.getTeams().stream()
                .map(Team::getName)
                .collect(Collectors.toList()));
        return playerDto;
    }

    public Player getPlayerEntityByName(String name) {
        Optional<Player> playerOptional = playerRepository.findByName(name);
        if (playerOptional.isEmpty()) {
            throw new PlayerNotFoundException(name);
        }
        return playerOptional.get();
    }

    public PlayerDTO createPlayer(PlayerDTO playerDto) throws PlayerAlreadyExistsException, TeamNotFoundException {
        if (playerDto.getName() == null || playerDto.getPosition() == null) {
            throw new IllegalArgumentException("Arguments name and position cannot be empty!");
        }
        try {
            String nameProvided = playerDto.getName();
            getPlayerByName(nameProvided);

            throw new PlayerAlreadyExistsException(nameProvided);
        }
        catch (PlayerNotFoundException e) {
            List<Team> teams = teamRepository.findAllByNameIn(playerDto.getTeams());

            if (teams.size() != playerDto.getTeams().size()) {
                throw new TeamNotFoundException();
            }
            Player player = new Player();
            player.setName(playerDto.getName());
            player.setPosition(playerDto.getPosition());
            player.setTeams(teams);

            Player savedPlayer = playerRepository.save(player);

            playerDto.setId(savedPlayer.getId());

            return playerDto;
        }
    }

    public List<PlayerDTO> searchWithFilters(String name, String position, Integer numGoals, Integer numCards, Integer numGames) {
        List<Player> players;
        if (name == null || name.equals(""))
            players = playerRepository.findAll();
        else 
            players = playerRepository.findByNameStartingWith(name);
        List<PlayerDTO> matchingNamePlayers = players.stream().map(this::mapToDto).toList();
        List<PlayerDTO> matchingPlayers = new LinkedList<>();
        for (PlayerDTO player : matchingNamePlayers) {
            if (!position.equals("") && !Position.toString(player.getPosition()).equals(position))
                continue;
            if (numGoals != null && matchStatsHandler.getNumberGoalsOfPlayer(player.getName()) != numGoals)
                continue;
            if (numCards != null && (matchStatsRepository.countYellowCardsByPlayerId(player.getId()) + 
             matchStatsRepository.countRedCardsByPlayerId(player.getId())) != numCards)
                continue;
            if (numGames != null && matchRepository.countMatchesByPlayer(player.getId()) != numGames)
                continue;
            matchingPlayers.add(player);
        }

        return matchingPlayers;
    }
}
