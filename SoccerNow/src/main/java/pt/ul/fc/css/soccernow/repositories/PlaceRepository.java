package pt.ul.fc.css.soccernow.repositories;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pt.ul.fc.css.soccernow.entities.Place;


	public interface PlaceRepository extends JpaRepository<Place, Long>{
		Optional<Place> findById(long id);

		List<Place> findByStadiumAndDateTime(String stadium, LocalDateTime dateTime);

		// Fast existence check (uses simple index)
    	boolean existsByStadium(String stadium);
    
    // Optimized window query (uses compound index)
		 @Query("SELECT COUNT(p) > 0 FROM Place p WHERE " +
          	"p.stadium = :stadium AND " +
           	"p.dateTime BETWEEN :start AND :end")
    	boolean existsConflictingPlace(
        	@Param("stadium") String stadium,
        	@Param("start") LocalDateTime start,
        	@Param("end") LocalDateTime end);

	}
	
