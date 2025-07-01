package pt.ul.fc.css.soccernow.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.Player;

public interface PlayerRepository extends JpaRepository<Player, Long>{
	@Query("SELECT p FROM Player p WHERE p.name = :name")
	Optional<Player> findByName(String name);

	@Query("SELECT p FROM Player p WHERE LOWER(p.name) LIKE LOWER(CONCAT(:prefix, '%'))")
	List<Player> findByNameStartingWith(@Param("prefix") String prefix);



	@Modifying
	@Query("DELETE FROM Player p WHERE p.name = :name")
	void deleteByName(@Param("name") String name);

	List<Player> findByTeams_Name(String name);


	@Query("SELECT COUNT(m) > 0 FROM Match m " +
		"WHERE (m.lineUp1.captain = :player OR " +
		"m.lineUp1.goalkeeper = :player OR " +
		"m.lineUp1.defender = :player OR " +
		"m.lineUp1.rightWinger = :player OR " +
		"m.lineUp1.leftWinger = :player OR " +
		"m.lineUp1.pivot = :player OR " +
		"m.lineUp2.captain = :player OR " +
		"m.lineUp2.goalkeeper = :player OR " +
		"m.lineUp2.defender = :player OR " +
		"m.lineUp2.rightWinger = :player OR " +
		"m.lineUp2.leftWinger = :player OR " +
		"m.lineUp2.pivot = :player) " +
		"AND m.place.dateTime BETWEEN :start AND :end")
	boolean existsByPlayerAndDateTimeBetween(@Param("player") Player player,
										@Param("start") LocalDateTime start,
										@Param("end") LocalDateTime end);
}
