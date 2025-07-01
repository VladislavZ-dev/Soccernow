package pt.ul.fc.css.soccernow.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.MatchStats;
import pt.ul.fc.css.soccernow.entities.Player;

public interface MatchStatsRepository extends JpaRepository<MatchStats, Long>{
	Optional<MatchStats> findById(long id);

	@Query("SELECT p.id as playerId, p.name as playerName, COUNT(p) as redCardCount " +
           "FROM MatchStats ms JOIN ms.redCards p " +
           "GROUP BY p.id, p.name " +
           "ORDER BY redCardCount DESC")
    List<PlayerRedCardProjection> findPlayersByRedCardsDesc();

   interface PlayerRedCardProjection {
      Long getPlayerId();
      String getPlayerName();
      Long getRedCardCount();
   }

   @Query("SELECT p.id as playerId, p.name as playerName, COUNT(p) as redCardCount " +
           "FROM MatchStats ms JOIN ms.redCards p " +
           "GROUP BY p.id, p.name " +
           "ORDER BY redCardCount DESC")
    List<PlayerYellowCardProjection> findPlayersByYellowCardsDesc();

   interface PlayerYellowCardProjection {
      Long getPlayerId();
      String getPlayerName();
      Long getYellowCardCount();
   }

   @Query("SELECT COUNT(ms) FROM MatchStats ms JOIN ms.redCards p WHERE p.id = :playerId")
   Long countRedCardsByPlayerId(long playerId);

   @Query("SELECT COUNT(ms) FROM MatchStats ms JOIN ms.yellowCards p WHERE p.id = :playerId")
   Long countYellowCardsByPlayerId(long playerId);

   @Query("SELECT DISTINCT ms FROM MatchStats ms " +
         "LEFT JOIN FETCH ms.yellowCards " +
         "LEFT JOIN FETCH ms.match m")
   List<MatchStats> findAllWithYellowCards();

   @Query("SELECT DISTINCT ms FROM MatchStats ms " +
         "LEFT JOIN FETCH ms.redCards " +
         "LEFT JOIN FETCH ms.match m")
   List<MatchStats> findAllWithRedCards();


   @Query("SELECT DISTINCT ms FROM MatchStats ms " +
      "JOIN ms.match m " +
      "JOIN m.lineUp1 l1 " +
      "JOIN m.lineUp2 l2 " +
      "WHERE :player IN (l1.captain, l1.pivot, l1.leftWinger, l1.rightWinger, l1.defender, l1.goalkeeper) " +
      "OR :player IN (l2.captain, l2.pivot, l2.leftWinger, l2.rightWinger, l2.defender, l2.goalkeeper)")
   List<MatchStats> findByPlayerInLineups(Player player);

   @Query("SELECT ms FROM MatchStats ms WHERE ms.winner IS NOT NULL AND ms.winner <> ''")
   List<MatchStats> findAllWithWinners();

   @Query("SELECT DISTINCT ms FROM MatchStats ms JOIN ms.playerGoals pg WHERE KEY(pg).id = :playerId")
   List<MatchStats> findMatchesWithPlayerGoals(@Param("playerId") Long playerId);
}
