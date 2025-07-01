package pt.ul.fc.css.soccernow.tests;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.entities.Position;
import pt.ul.fc.css.soccernow.handlers.PlayerHandler;
import pt.ul.fc.css.soccernow.handlers.TeamHandler;

@Component
public class TeamSanityTests {

    @Autowired
    TeamHandler teamHandler;

    @Autowired
    PlayerHandler playerHandler;

    public void test1() {

        TeamDto rogersTeam = new TeamDto(0, "buff rogers", 0, null);
        TeamDto ballersTeam = new TeamDto(0, "blue ballers", 0, null);
        TeamDto nullTeam = new TeamDto();

        TeamDto rogersResponse = teamHandler.createNewTeam(rogersTeam);
        TeamDto ballersResponse = teamHandler.createNewTeam(ballersTeam);
        TeamDto nullResponse = teamHandler.createNewTeam(nullTeam);
        TeamDto rogers2 = teamHandler.createNewTeam(rogersTeam);

        if (rogersResponse == null) {
            System.out.println("test1 failed, rogers was not created");
            return;
        }
        if (ballersResponse == null) {
            System.out.println("test1 failed, ballers was not created");
            return;
        }
        if (nullResponse != null) {
            System.out.println("test1 failed, nullTeam somehow was created");
            return;
        }
        if (rogers2 != null) {
            return;
        }

        System.out.println("test1 passed successfully");
    }


    public void test2() {
        TeamDto rogers = new TeamDto(0, "buff rogers", 0, null);
        TeamDto ballers = new TeamDto(0, "blue ballers", 0, null);

        teamHandler.createNewTeam(rogers);
        teamHandler.createNewTeam(ballers);

        TeamDto rogersTeam = teamHandler.searchTeam("buff rogers");
        TeamDto ballersTeam = teamHandler.searchTeam("blue ballers");
        TeamDto lionsTeam = teamHandler.searchTeam("mighty lions");

        if (rogersTeam == null) {
            System.out.println("test2 failed, rogers could not be searched");
            return;
        }
        if (ballersTeam == null) {
            System.out.println("test2 failed, ballers could not be searched");
            return;
        }
        if (!rogersTeam.getName().equals("buff rogers")) {
            System.out.println("test2 failed, rogers doesn't have the correct name");
            return;
        }
        if (!ballersTeam.getName().equals("blue ballers")) {
            System.out.println("test2 failed, ballers doesn't have the correct name");
            return;
        }
        if (lionsTeam != null) {
            System.out.println("test2 failed, lions should't be searched");
            return;
        }

        System.out.println("test2 passed successfully");

    }


    public void test3() {
        TeamDto rogers = new TeamDto(0, "buff rogers", 0, null);
        TeamDto ballers = new TeamDto(0, "blue ballers", 0, null);

        teamHandler.createNewTeam(rogers);
        teamHandler.createNewTeam(ballers);

        teamHandler.removeTeam("buff rogers");
        TeamDto rogersTeam = teamHandler.searchTeam("buff rogers");
        TeamDto ballersTeam = teamHandler.searchTeam("blue ballers");

        if (rogersTeam != null) {
            System.out.println("test2 failed, rogers could not be searched");
            return;
        }
        if (ballersTeam == null) {
            System.out.println("test2 failed, ballers could not be searched");
            return;
        }

        teamHandler.removeTeam("blue ballers");
        teamHandler.removeTeam("mighty lions");
        int teamsAmount = teamHandler.getAllTeams().size();

        if (teamsAmount != 0) {
            System.out.println("test2 failed, rogers doesn't have the correct name");
            return;
        }

        System.out.println("test3 passed successfully");
    }


    @Transactional
    public void test4() {
        TeamDto rogers = new TeamDto(0, "buff rogers", 0, null);
        TeamDto ballers = new TeamDto(0, "blue ballers", 0, null);

        teamHandler.createNewTeam(rogers);
        teamHandler.createNewTeam(ballers);

        TeamDto rogersFeederClub = new TeamDto(ballers.getId(), "buff rogers feeder club", ballers.getHistoryId(), ballers.getPlayers());
        teamHandler.updateTeam(rogersFeederClub, "blue ballers");

        TeamDto rogerFeeders = teamHandler.searchTeam("buff rogers feeder club");
        TeamDto ballersTeam = teamHandler.searchTeam("blue ballers");

        if (rogerFeeders == null) {
            System.out.println("test2 failed, rogers could not be searched");
            return;
        }
        if (ballersTeam != null) {
            System.out.println("test2 failed, ballers could not be searched");
            return;
        }

        TeamDto feedersGone = new TeamDto(rogerFeeders.getId(), "", rogerFeeders.getHistoryId(), rogerFeeders.getPlayers());
        teamHandler.updateTeam(feedersGone, "buff rogers feeder club");

        int teamsAmount = teamHandler.getAllTeams().size();

        if (teamsAmount != 2) {
            return;
        }

        System.out.println("test4 passed successfully");
    }

    @Transactional
    public void test5() {

        TeamDto rogers = new TeamDto(0, "buff rogers", 0, null);
        TeamDto ballers = new TeamDto(0, "blue ballers", 0, null);

        teamHandler.createNewTeam(rogers);
        teamHandler.createNewTeam(ballers);

        PlayerDTO vicente = new PlayerDTO();
        vicente.setName("Vicente");
        vicente.setPosition(Position.RIGHT_WINGER);
        vicente.setTeams(Collections.singletonList("buff rogers"));
        playerHandler.createPlayer(vicente);

        PlayerDTO zeUlisses = new PlayerDTO();
        zeUlisses.setName("Zé Ulisses");
        zeUlisses.setPosition(Position.LEFT_WINGER);
        zeUlisses.setTeams(Collections.singletonList("buff rogers"));
        playerHandler.createPlayer(zeUlisses);

        PlayerDTO bernardino = new PlayerDTO();
        bernardino.setName("Bernardino");
        bernardino.setPosition(Position.DEFENDER);
        bernardino.setTeams(Collections.singletonList("blue ballers"));
        playerHandler.createPlayer(bernardino);

        int numRogers = teamHandler.getNumberOfPlayersOfTeam("buff rogers");
        if (numRogers != 2) {
            return;
        }

        String nomeDoBlueBaller = teamHandler.searchTeam("blue ballers").getPlayers().get(0);
        if (!nomeDoBlueBaller.equals("Bernardino")) {
            return;
        }

        teamHandler.removeTeam("buff rogers");

        vicente = playerHandler.getPlayerByName("vicente");
        if (vicente == null) {
            return;
        }

        System.out.println("test5 passed successfully");
    }

}
