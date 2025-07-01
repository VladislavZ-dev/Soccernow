package pt.ul.fc.css.soccernow.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.Place;
import pt.ul.fc.css.soccernow.entities.Referee;

public interface MatchRepository extends JpaRepository<Match, Long>{
	Optional<Match> findById(long id);

	long countByRef(Referee referee);

	@Query("SELECT r FROM Referee r " +
       "WHERE r.id IN (" +
       "   SELECT m.ref.id FROM Match m " +
       "   GROUP BY m.ref.id " +
       "   ORDER BY COUNT(m.ref) DESC" +
       ")")
	List<Referee> findRefereesByMatchCountDesc();

	List<Match> findByPlace(Place place);

    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE " +
           "m.place = :place AND " +
           "m.place.dateTime BETWEEN :start AND :end")
    boolean existsByPlaceAndDateTimeBetween(
        @Param("place") Place place,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end);

	@Query("SELECT m FROM Match m WHERE m.place.dateTime > CURRENT_TIMESTAMP")
	List<Match> findFutureMatches();

       @Query("""
       SELECT COUNT(m)
       FROM Match m
       WHERE 
       m.lineUp1.goalkeeper.id = :playerId OR
       m.lineUp1.defender.id = :playerId OR
       m.lineUp1.rightWinger.id = :playerId OR
       m.lineUp1.leftWinger.id = :playerId OR
       m.lineUp1.pivot.id = :playerId OR
       m.lineUp2.goalkeeper.id = :playerId OR
       m.lineUp2.defender.id = :playerId OR
       m.lineUp2.rightWinger.id = :playerId OR
       m.lineUp2.leftWinger.id = :playerId OR
       m.lineUp2.pivot.id = :playerId
       """)
       Integer countMatchesByPlayer(@Param("playerId") Long playerId);

       @Query("SELECT COUNT(m) FROM Match m WHERE m.ref.id = :refereeId")
       Integer countMatchesByReferee(@Param("refereeId") Long refereeId);

       @Query("SELECT m FROM Match m WHERE m.ref.id = :refereeId")
       List<Match> findMatchesOfReferee(@Param("refereeId") Long refereeId);

       @Query("SELECT m FROM Match m WHERE LOWER(m.place.stadium) LIKE LOWER(CONCAT('%', :stadiumPart, '%'))")
       List<Match> findMatchesByStadium(@Param("stadiumPart") String stadiumPart);

      
       @Query("SELECT COUNT(m) > 0 FROM Match m WHERE m.ref = :ref AND m.place.dateTime BETWEEN :start AND :end")
       boolean existsByRefAndDateTimeBetween(
              @Param("ref") Referee ref,
              @Param("start") LocalDateTime start, 
              @Param("end") LocalDateTime end);
}
