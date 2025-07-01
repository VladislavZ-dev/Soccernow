package pt.ul.fc.css.soccernow.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT team FROM Team team WHERE LOWER(team.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Team> findByName(@Param("name") String name);


    void deleteByName(String name);

    List<Team> findAllByNameIn(List<String> names);

    Optional<Team> findByNameEquals(String name);

}