package pt.ul.fc.css.soccernow.handlers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ul.fc.css.soccernow.dto.LineUpDto;
import pt.ul.fc.css.soccernow.entities.LineUp;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.repositories.LineUpRepository;


@Service
public class LineUpHandler {

	@Autowired
	private LineUpRepository lineUpRepository;

    @Autowired
	private TeamHandler teamHandler;

    @Autowired
	private PlayerHandler playerHandler;

    @Autowired
	private MatchHandler matchHandler;

    public LineUpDto saveLineUp(LineUp lineUp) {
        return mapToDto(lineUpRepository.save(lineUp));
    }

    public LineUpDto getLineUpById(long id) {
        Optional<LineUp> LineUpOptional = lineUpRepository.findById(id);
        if (LineUpOptional.isEmpty()) {
            return null;
        }
        return mapToDto(LineUpOptional.get());
    }

    public LineUpDto mapToDto(LineUp lineUp) {
        LineUpDto dto = new LineUpDto();
        dto.setTeam(lineUp.getTeam().getName());
        dto.setMatchId(lineUp.getMatch().getId());
        dto.setCaptain(lineUp.getCaptain().getName());
        dto.setPivot(lineUp.getPivot().getName());
        dto.setLeftWinger(lineUp.getLeftWinger().getName());
        dto.setRightWinger(lineUp.getRightWinger().getName());
        dto.setDefender(lineUp.getDefender().getName());
        dto.setGoalkeeper(lineUp.getGoalkeeper().getName());
        return dto;
    }

    public LineUp createLineUp(LineUpDto lineUpDto) {
    	Team team = teamHandler.searchTeamEntity(lineUpDto.getTeam());
    	Match match = matchHandler.getMatchByIdEntity(lineUpDto.getMatchId());
    	Player captain = playerHandler.getPlayerEntityByName(lineUpDto.getCaptain());
    	Player pivot = playerHandler.getPlayerEntityByName(lineUpDto.getPivot());
    	Player leftWinger = playerHandler.getPlayerEntityByName(lineUpDto.getLeftWinger());
    	Player rightWinger = playerHandler.getPlayerEntityByName(lineUpDto.getRightWinger());
    	Player defender = playerHandler.getPlayerEntityByName(lineUpDto.getDefender());
    	Player goalKeeper = playerHandler.getPlayerEntityByName(lineUpDto.getGoalkeeper());
    	LineUp lineUp = new LineUp();
    	lineUp.setTeam(team);
    	lineUp.setMatch(match);
    	lineUp.setCaptain(captain);
    	lineUp.setPivot(pivot);
    	lineUp.setLeftWinger(leftWinger);
    	lineUp.setRightWinger(rightWinger);
    	lineUp.setDefender(defender);
    	lineUp.setGoalkeeper(goalKeeper);

    	return lineUpRepository.save(lineUp);
    }
}
