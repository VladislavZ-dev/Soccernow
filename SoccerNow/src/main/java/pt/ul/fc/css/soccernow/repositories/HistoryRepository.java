package pt.ul.fc.css.soccernow.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.History;
import pt.ul.fc.css.soccernow.entities.Tournament;

public interface HistoryRepository extends JpaRepository<History, Long>{

    @Query("SELECT h FROM History h JOIN h.tournamentPlacements tp WHERE KEY(tp) = :tournament")
    List<History> findByTournamentInPlacements(@Param("tournament") Tournament tournament);
}
