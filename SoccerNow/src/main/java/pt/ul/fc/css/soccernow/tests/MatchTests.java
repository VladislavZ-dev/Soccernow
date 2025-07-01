package pt.ul.fc.css.soccernow.tests;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.ResponseEntity;

import pt.ul.fc.css.soccernow.controllers.MatchController;
import pt.ul.fc.css.soccernow.controllers.PlayerController;
import pt.ul.fc.css.soccernow.controllers.RefereeController;
import pt.ul.fc.css.soccernow.controllers.TeamController;
import pt.ul.fc.css.soccernow.dto.PlaceDto;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.entities.Certificate;
import pt.ul.fc.css.soccernow.entities.Position;
import pt.ul.fc.css.soccernow.handlers.PlaceHandler;

public class MatchTests {

	
	private RefereeController refereeController;
	private PlayerController playerController;
	private TeamController teamController;
	private MatchController matchController;
	
	private PlaceHandler placeHandler;
	public void createMatch () {
		RefereeDTO refeDto = new RefereeDTO("refe Pablo", (long) 1, Certificate.CERTIFIED);
		ResponseEntity<RefereeDTO> refeResponse= refereeController.createReferee(refeDto);
		List<String> teams = new LinkedList<>();
		teams.add("ya");
		
		PlayerDTO playerDto = new PlayerDTO("",(long) 1, Position.GOALKEEPER, teams );
		
		PlaceDto placeDto = new PlaceDto((long) 1, "Estadio dos mirtilos", LocalDateTime.now() );
		
		ResponseEntity<?> match = matchController.createMatch(null);
	}
}