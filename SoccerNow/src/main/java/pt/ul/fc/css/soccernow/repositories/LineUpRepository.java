package pt.ul.fc.css.soccernow.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.LineUp;

public interface LineUpRepository extends JpaRepository<LineUp, Long>{
    Optional<LineUp> findById(long id);

    @Query("SELECT l FROM LineUp l WHERE l.team.name = :teamName")
    List<LineUp> findLineUpsByTeamName(@Param("teamName") String teamName);

}
