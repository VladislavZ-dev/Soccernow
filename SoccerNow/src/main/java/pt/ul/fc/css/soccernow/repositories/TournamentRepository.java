package pt.ul.fc.css.soccernow.repositories;

import java.util.List;
import java.util.Optional;  

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.entities.Tournament;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Optional<Tournament> findById(long id);

    @Query("SELECT t FROM Tournament t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tournament> findByName(@Param("name") String name);

    @Transactional
    void deleteByName(String name);

    @Transactional
    void deleteById(long id);

    boolean existsById(long id);

    // Find tournaments by exact name (useful for checking duplicates)
    @Query("SELECT t FROM Tournament t WHERE LOWER(t.name) = LOWER(:name)")
    List<Tournament> findByExactName(@Param("name") String name);

    // Find tournaments containing specific team
    @Query("SELECT t FROM Tournament t JOIN t.teams team WHERE team.name = :teamName")
    List<Tournament> findTournamentsByTeam(@Param("teamName") String teamName);

    // Find tournaments with scheduled matches
    @Query("SELECT t FROM Tournament t WHERE SIZE(t.scheduledMatches) > 0")
    List<Tournament> findTournamentsWithScheduledMatches();

    // Find tournaments with played matches
    @Query("SELECT t FROM Tournament t WHERE SIZE(t.playedMatches) > 0")
    List<Tournament> findTournamentsWithPlayedMatches();

    // Find tournaments by team count
    @Query("SELECT t FROM Tournament t WHERE SIZE(t.teams) = :teamCount")
    List<Tournament> findTournamentsByTeamCount(@Param("teamCount") int teamCount);

    // Find tournaments with minimum number of teams
    @Query("SELECT t FROM Tournament t WHERE SIZE(t.teams) >= :minTeams")
    List<Tournament> findTournamentsWithMinimumTeams(@Param("minTeams") int minTeams);

    // Custom query to find all tournaments ordered by name
    @Query("SELECT t FROM Tournament t ORDER BY t.name ASC")
    List<Tournament> findAllOrderedByName();

    // Find tournaments that contain any of the specified team names
    @Query("SELECT DISTINCT t FROM Tournament t JOIN t.teams team WHERE team.name IN :teamNames")
    List<Tournament> findTournamentsByTeamNames(@Param("teamNames") List<String> teamNames);

    @Query("SELECT DISTINCT t FROM Tournament t " +
         "LEFT JOIN t.teams team " +
         "WHERE (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
         "AND (:teamName IS NULL OR LOWER(team.name) LIKE LOWER(CONCAT('%', :teamName, '%')))")
   List<Tournament> findWithFilters(@Param("name") String name, 
                                    @Param("teamName") String teamName);

}