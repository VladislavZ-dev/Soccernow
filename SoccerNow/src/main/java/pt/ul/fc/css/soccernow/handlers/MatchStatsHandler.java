package pt.ul.fc.css.soccernow.handlers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.dto.MatchStatsDto;
import pt.ul.fc.css.soccernow.entities.LineUp;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.MatchStats;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.exceptions.PlayerNotFoundException;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;
import pt.ul.fc.css.soccernow.repositories.MatchStatsRepository;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;

@Service
@Transactional
public class MatchStatsHandler {
    @Autowired
    private MatchStatsRepository matchStatsRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    public MatchStatsDto findMatchStats(long id) {
        return matchStatsRepository.findById(id)
            .map(this::mapToDto)
            .orElseThrow(() -> new NoSuchElementException("MatchStats not found for id: " + id));
    }

    public MatchStats findMatchStatsEntity(long id) {
        return matchStatsRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("MatchStats not found for id: " + id));
    }

    public MatchStats createMatchStats(Match match) {
        MatchStats stats = new MatchStats();
        stats.setMatch(match);
        stats.setWinner("Draw");
        return matchStatsRepository.save(stats);
    }

    public MatchStatsDto mapToDto(MatchStats matchStats) {
        MatchStatsDto newMatchStatsDto = new MatchStatsDto(matchStats.getId(), matchStats.getMatch().getId(), matchStats.getTeam1Score(),
        		matchStats.getTeam2Score(), matchStats.getWinner(), getPlayersNames(matchStats.getYellowCards()), getPlayersNames(matchStats.getRedCards()));
        return newMatchStatsDto;
    }

        private List<String> getPlayersNames(List<Player> players) {
        List<String> playersNames = new LinkedList<>();
        for (Player player : players)
        	playersNames.add(player.getName());
        return playersNames;
    }

    public void recordPlayerGoals(Match match, Player player, int goals) {
        MatchStats stats = match.getStats();
        if (stats == null) {
            stats = new MatchStats();
            match.setStats(stats);
        }
        stats.addPlayerGoals(player, goals);
        stats.updateTeamScores();
        matchStatsRepository.save(stats);
    }

    public int getPlayerGoalsInMatch(Match match, Player player) {
        return match.getStats() != null ?
            match.getStats().getPlayerGoals().getOrDefault(player, 0) : 0;
    }

    public String getPlayersWithMostRedCards() {
        StringBuilder sb = new StringBuilder("Players with most red cards:\n");
        matchStatsRepository.findPlayersByRedCardsDesc().forEach(projection -> {
            sb.append(String.format("- %s: %d red cards\n",
                projection.getPlayerName(),
                projection.getRedCardCount()));
        });
        return sb.toString();
    }

    public String getPlayersWithMostYellowCards() {
        StringBuilder sb = new StringBuilder("Players with most yellow cards:\n");
        matchStatsRepository.findPlayersByYellowCardsDesc().forEach(projection -> {
            sb.append(String.format("- %s: %d red cards\n",
                projection.getPlayerName(),
                projection.getYellowCardCount()));
        });
        return sb.toString();
    }

    public String getTeamsWithMostCards() {
        Map<String, Integer> teamCards = new HashMap<>();

        matchStatsRepository.findAllWithYellowCards().forEach(stats ->
            processMatchCards(stats, teamCards));

        return formatTeamStats(sortByValueDesc(teamCards), "Teams with most cards");
    }

    private void processMatchCards(MatchStats stats, Map<String, Integer> teamCards) {
        Set<Player> uniquePlayers = new HashSet<>(stats.getYellowCards());
        for (Player player : uniquePlayers) {
            String teamName = findPlayerTeam(stats.getMatch(), player);
            if (teamName != null)
                teamCards.merge(teamName, 1, Integer::sum);
        }

        Set<Player> uniquePlayersRed = new HashSet<>(stats.getRedCards());
        for (Player player : uniquePlayersRed) {
            String teamName = findPlayerTeam(stats.getMatch(), player);
            if (teamName != null)
                teamCards.merge(teamName, 1, Integer::sum);
        }
    }


    private String findPlayerTeam(Match match, Player player) {
        if (match.getLineUp1().getPlayers().contains(player)) {
            return match.getLineUp1().getTeam().getName();
        } else if (match.getLineUp2().getPlayers().contains(player)) {
            return match.getLineUp2().getTeam().getName();
    }
    return "Unknown";
}

    private void processLineupCards(MatchStats stats, LineUp lineup, Map<String, Integer> teamCards) {
        String teamName = lineup.getTeam().getName();
        List<Player> players = lineup.getPlayers();

        long yellows = stats.getYellowCards().stream()
            .filter(players::contains)
            .count();

        long reds = stats.getRedCards().stream()
            .filter(players::contains)
            .count();

        teamCards.merge(teamName, (int) (yellows + reds), Integer::sum);
    }

    public double getAverageGoalsPerGameForPlayer(String playerName) {
        Player player = playerRepository.findByName(playerName)
            .orElseThrow(() -> new PlayerNotFoundException(playerName));

        List<MatchStats> playerMatches = matchStatsRepository.findMatchesWithPlayerGoals(player.getId());

        if (playerMatches.isEmpty()) return 0.0;

        int totalGoals = playerMatches.stream()
            .mapToInt(stats -> stats.getPlayerGoals().getOrDefault(player, 0))
            .sum();

        return (double) totalGoals / playerMatches.size();
    }

    public int getNumberGoalsOfPlayer(String playerName) {
        Player player = playerRepository.findByName(playerName)
            .orElseThrow(() -> new PlayerNotFoundException(playerName));

        List<MatchStats> playerMatches = matchStatsRepository.findMatchesWithPlayerGoals(player.getId());

        if (playerMatches.isEmpty()) return 0;

        return playerMatches.stream()
            .mapToInt(stats -> stats.getPlayerGoals().getOrDefault(player, 0))
            .sum();
    }

    public String getTeamsWithMostWins() {
        Map<String, Integer> wins = new HashMap<>();
        matchStatsRepository.findAllWithWinners().forEach(stats -> {
            if (stats.getWinner() != null && !stats.getWinner().equals("Draw") && !stats.getWinner().isEmpty()) {
                wins.merge(stats.getWinner(), 1, Integer::sum);
            }
        });
        return formatTeamStats(sortByValueDesc(wins), "Teams with most wins");
    }

    private String formatTeamStats(Map<String, Integer> stats, String title) {
        StringBuilder sb = new StringBuilder(title).append(":\n");
        stats.forEach((team, count) ->
            sb.append(String.format("- %s: %d\n", team, count)));
        return sb.toString();
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
        return map.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    public String registerCard(Match match, Player player, boolean isRedCard) {
        MatchStats stats = match.getStats();
        if (stats == null) {
            stats = new MatchStats();
            stats.setMatch(match);
            match.setStats(stats);
        }

        if (isRedCard) {
            if (!stats.getRedCards().contains(player)) {
                stats.getRedCards().add(player);
            }
        } else {
            if (!stats.getYellowCards().contains(player)) {
                stats.getYellowCards().add(player);
            }
        }

        matchStatsRepository.save(stats);

        return "Card was successfully registered for " + player.getName();
    }

    public String registerCard(long matchId, String playerName, boolean isRedCard) {
        Match match = matchRepository.findById(matchId).get();
        Player player = playerRepository.findByName(playerName).get();

        MatchStats stats = match.getStats();
        if (stats == null) {
            stats = new MatchStats();
            stats.setMatch(match);
            match.setStats(stats);
        }

        if (isRedCard) {
            if (!stats.getRedCards().contains(player)) {
                stats.getRedCards().add(player);
            }
        } else {
            if (!stats.getYellowCards().contains(player)) {
                stats.getYellowCards().add(player);
            }
        }

        matchStatsRepository.save(stats);

        return "Card was successfully registered for " + player.getName();
    }

    public long countRedCardsByPlayerId(long playerId) {
        return matchStatsRepository.countRedCardsByPlayerId(playerId);
    }

    public long countYellowCardsByPlayerId(long playerId) {
        return matchStatsRepository.countYellowCardsByPlayerId(playerId);
    }

}
