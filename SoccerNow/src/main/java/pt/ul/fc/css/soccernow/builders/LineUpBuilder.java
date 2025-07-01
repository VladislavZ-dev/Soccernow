package pt.ul.fc.css.soccernow.builders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ul.fc.css.soccernow.dto.LineUpDto;
import pt.ul.fc.css.soccernow.entities.LineUp;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.handlers.PlayerHandler;
import pt.ul.fc.css.soccernow.handlers.TeamHandler;

@Service
public class LineUpBuilder {

    @Autowired
    private TeamHandler teamHandler;

    @Autowired
    private PlayerHandler playerHandler;

    public LineUp buildLineUp(LineUpDto lineUpDto, Match match) {
        Team team = teamHandler.searchTeamEntity(lineUpDto.getTeam());
        Player captain = playerHandler.getPlayerEntityByName(lineUpDto.getCaptain());
        Player pivot = playerHandler.getPlayerEntityByName(lineUpDto.getPivot());
        Player leftWinger = playerHandler.getPlayerEntityByName(lineUpDto.getLeftWinger());
        Player rightWinger = playerHandler.getPlayerEntityByName(lineUpDto.getRightWinger());
        Player defender = playerHandler.getPlayerEntityByName(lineUpDto.getDefender());
        Player goalkeeper = playerHandler.getPlayerEntityByName(lineUpDto.getGoalkeeper());

        LineUp lineUp = new LineUp();
        lineUp.setTeam(team);
        lineUp.setMatch(match);
        lineUp.setCaptain(captain);
        lineUp.setPivot(pivot);
        lineUp.setLeftWinger(leftWinger);
        lineUp.setRightWinger(rightWinger);
        lineUp.setDefender(defender);
        lineUp.setGoalkeeper(goalkeeper);

        return lineUp;
    }
}

