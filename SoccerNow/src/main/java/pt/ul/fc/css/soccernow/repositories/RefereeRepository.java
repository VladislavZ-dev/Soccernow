package pt.ul.fc.css.soccernow.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.Referee;

public interface RefereeRepository extends JpaRepository<Referee, Long>{
	@Query("SELECT r FROM Referee r WHERE r.name = :name")
	Optional<Referee> findByName(String name);

	@Query("SELECT r FROM Referee r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :prefix, '%'))")
	List<Referee> findByNameStartingWith(@Param("prefix") String prefix);


	@Modifying
	@Query("DELETE FROM Referee r WHERE r.name = :name")
	void deleteByName(@Param("name") String name);
}