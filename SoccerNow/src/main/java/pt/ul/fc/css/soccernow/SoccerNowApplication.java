package pt.ul.fc.css.soccernow;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.dto.LineUpDto;
import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.dto.PlaceDto;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.dto.TournamentDto;
import pt.ul.fc.css.soccernow.entities.Certificate;
import pt.ul.fc.css.soccernow.entities.Position;
import pt.ul.fc.css.soccernow.entities.TournamentType;
import pt.ul.fc.css.soccernow.handlers.MatchHandler;
import pt.ul.fc.css.soccernow.handlers.MatchStatsHandler;
import pt.ul.fc.css.soccernow.handlers.PlaceHandler;
import pt.ul.fc.css.soccernow.handlers.PlayerHandler;
import pt.ul.fc.css.soccernow.handlers.RefereeHandler;
import pt.ul.fc.css.soccernow.handlers.TeamHandler;
import pt.ul.fc.css.soccernow.handlers.TournamentHandler;
import pt.ul.fc.css.soccernow.tests.TeamSanityTests;

@SpringBootApplication
public class SoccerNowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoccerNowApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner demo(TeamHandler teamHandler, PlayerHandler playerHandler,
                                RefereeHandler refereeHandler, PlaceHandler placeHandler, TeamSanityTests teamTests,
                                MatchHandler matchHandler, MatchStatsHandler matchStatsHandler, TournamentHandler tournamentHandler) {
        return (args) -> {
            System.out.println("Running sanity tests...");

            //teamTests.test1();
            //teamTests.test2();
            //teamTests.test3();
            //teamTests.test4();
            //these tests passed

            TeamDto buffRogers = teamHandler.createNewTeam(new TeamDto(0, "Buff Rogers", 1, null)); //estadio do canguru
            TeamDto blueBallers = teamHandler.createNewTeam(new TeamDto(0, "Blue Ballers", 2, null)); //estadio da luz azul
            TeamDto imperiusRomanus = teamHandler.createNewTeam(new TeamDto(0, "Imperius Romanus", 3, null)); //nova roma
            TeamDto technoVikings = teamHandler.createNewTeam(new TeamDto(0, "Techno Vikings", 4, null)); //valhalla field
            TeamDto ancientPharaohs = teamHandler.createNewTeam(new TeamDto(0, "Ancient Pharaohs", 5, null)); //sahara arena
            TeamDto arcticBoys = teamHandler.createNewTeam(new TeamDto(0, "Arctic Boys", 6, null)); //antartida
            TeamDto vodkaBeavers = teamHandler.createNewTeam(new TeamDto(0, "Vodka Beavers", 7, null)); //bobr lake
            TeamDto kamikazeDivers = teamHandler.createNewTeam(new TeamDto(0, "Kamikaze Divers", 8, null)); //onigashima

            PlayerDTO player1 = new PlayerDTO();
            player1.setName("Vicente");
            player1.setPosition(Position.PIVOT);
            player1.setTeams(Collections.singletonList("Buff Rogers"));
            playerHandler.createPlayer(player1);

            PlayerDTO player2 = new PlayerDTO();
            player2.setName("Arlindo");
            player2.setPosition(Position.LEFT_WINGER);
            player2.setTeams(Collections.singletonList("Buff Rogers"));
            playerHandler.createPlayer(player2);

            PlayerDTO player3 = new PlayerDTO();
            player3.setName("Diogo");
            player3.setPosition(Position.DEFENDER);
            player3.setTeams(Collections.singletonList("Buff Rogers"));
            playerHandler.createPlayer(player3);

            PlayerDTO player4 = new PlayerDTO();
            player4.setName("Manuel");
            player4.setPosition(Position.GOALKEEPER);
            player4.setTeams(Collections.singletonList("Buff Rogers"));
            playerHandler.createPlayer(player4);

            PlayerDTO player5 = new PlayerDTO();
            player5.setName("Guilherme");
            player5.setPosition(Position.RIGHT_WINGER);
            player5.setTeams(Collections.singletonList("Buff Rogers"));
            playerHandler.createPlayer(player5);

            // Create players for Blue Ballers team
            PlayerDTO player6 = new PlayerDTO();
            player6.setName("Williams");
            player6.setPosition(Position.LEFT_WINGER);
            player6.setTeams(Collections.singletonList("Blue Ballers"));
            playerHandler.createPlayer(player6);

            PlayerDTO player7 = new PlayerDTO();
            player7.setName("Donavan");
            player7.setPosition(Position.DEFENDER);
            player7.setTeams(Collections.singletonList("Blue Ballers"));
            playerHandler.createPlayer(player7);

            PlayerDTO player8 = new PlayerDTO();
            player8.setName("Dempsey");
            player8.setPosition(Position.RIGHT_WINGER);
            player8.setTeams(Collections.singletonList("Blue Ballers"));
            playerHandler.createPlayer(player8);

            PlayerDTO player9 = new PlayerDTO();
            player9.setName("Bellingham");
            player9.setPosition(Position.GOALKEEPER);
            player9.setTeams(Collections.singletonList("Blue Ballers"));
            playerHandler.createPlayer(player9);

            PlayerDTO player10 = new PlayerDTO();
            player10.setName("Kane");
            player10.setPosition(Position.PIVOT);
            player10.setTeams(Collections.singletonList("Blue Ballers"));
            playerHandler.createPlayer(player10);

            RefereeDTO referee1 = new RefereeDTO();
            referee1.setCertificate(Certificate.CERTIFIED);
            referee1.setName("Refe Bartolomeu");
            refereeHandler.createReferee(referee1);

            RefereeDTO referee2 = new RefereeDTO();
            referee2.setCertificate(Certificate.CERTIFIED);
            referee2.setName("Refe Mandioca");
            refereeHandler.createReferee(referee2);

            RefereeDTO referee3 = new RefereeDTO();
            referee3.setCertificate(Certificate.UNCERTIFIED);
            referee3.setName("Refe Asdrubal");
            refereeHandler.createReferee(referee3);

            RefereeDTO referee4 = new RefereeDTO();
            referee4.setCertificate(Certificate.UNCERTIFIED);
            referee4.setName("Refe Carlos");
            refereeHandler.createReferee(referee4);

            PlaceDto stadium = new PlaceDto();
            stadium.setStadium("Estadio do Canguru");
            stadium.setDateTime(LocalDateTime.of(2025, 5, 3, 5, 29, 45));
            PlaceDto createdPlace = placeHandler.createPlace(stadium);

            PlaceDto stadium2 = new PlaceDto();
            stadium2.setStadium("Estadio da Luz Azul");
            stadium2.setDateTime(LocalDateTime.of(2025, 6, 3, 5, 29, 45));
            PlaceDto createdPlace2 = placeHandler.createPlace(stadium2);

            LineUpDto homeLineup = new LineUpDto();
            homeLineup.setTeam("Buff Rogers");
            homeLineup.setCaptain("Vicente");
            homeLineup.setGoalkeeper("Manuel");
            homeLineup.setDefender("Diogo");
            homeLineup.setRightWinger("Guilherme");
            homeLineup.setLeftWinger("Arlindo");
            homeLineup.setPivot("Vicente");

            LineUpDto awayLineup = new LineUpDto();
            awayLineup.setTeam("Blue Ballers");
            awayLineup.setCaptain("Williams");
            awayLineup.setGoalkeeper("Bellingham");
            awayLineup.setDefender("Donavan");
            awayLineup.setRightWinger("Dempsey");
            awayLineup.setLeftWinger("Williams");
            awayLineup.setPivot("Kane");

            MatchDto matchDto = new MatchDto();
            matchDto.setPlaceId(createdPlace.getId());
            matchDto.setRefId(referee1.getId());
            matchDto.setLineUp1Id(homeLineup);
            matchDto.setLineUp2Id(awayLineup);

            matchHandler.createMatch(matchDto);

            PlayerDTO player11 = new PlayerDTO();
            player11.setName("Joel");
            player11.setPosition(Position.RIGHT_WINGER);
            player11.setTeams(Collections.singletonList("Buff Rogers"));
            playerHandler.createPlayer(player11);

            PlayerDTO player12 = new PlayerDTO();
            player12.setName("Figueiras");
            player12.setPosition(Position.DEFENDER);
            player12.setTeams(Collections.singletonList("Buff Rogers"));
            playerHandler.createPlayer(player12);

            PlayerDTO player13 = new PlayerDTO();
            player13.setName("Mctominay");
            player13.setPosition(Position.PIVOT);
            player13.setTeams(Collections.singletonList("Blue Ballers"));
            playerHandler.createPlayer(player13);

            PlayerDTO player14 = new PlayerDTO();
            player14.setName("Kent");
            player14.setPosition(Position.LEFT_WINGER);
            player14.setTeams(Collections.singletonList("Blue Ballers"));
            playerHandler.createPlayer(player14);

            LineUpDto homeLineup2 = new LineUpDto();
            homeLineup2.setTeam("Buff Rogers");
            homeLineup2.setCaptain("Vicente");
            homeLineup2.setGoalkeeper("Manuel");
            homeLineup2.setDefender("Figueiras");
            homeLineup2.setRightWinger("Joel");
            homeLineup2.setLeftWinger("Arlindo");
            homeLineup2.setPivot("Vicente");

            LineUpDto awayLineup2 = new LineUpDto();
            awayLineup2.setTeam("Blue Ballers");
            awayLineup2.setCaptain("Donavan");
            awayLineup2.setGoalkeeper("Bellingham");
            awayLineup2.setDefender("Donavan");
            awayLineup2.setRightWinger("Dempsey");
            awayLineup2.setLeftWinger("Kent");
            awayLineup2.setPivot("Mctominay");

            MatchDto matchDto2 = new MatchDto();
            matchDto2.setPlaceId(createdPlace2.getId());
            matchDto2.setRefId(referee3.getId());
            matchDto2.setLineUp1Id(awayLineup2);
            matchDto2.setLineUp2Id(homeLineup2);

            matchHandler.createMatch(matchDto2);


            PlayerDTO player15 = new PlayerDTO();
            player15.setName("Andronikus");
            player15.setPosition(Position.PIVOT);
            player15.setTeams(Collections.singletonList("Imperius Romanus"));
            playerHandler.createPlayer(player15);

            PlayerDTO player16 = new PlayerDTO();
            player16.setName("Leonius");
            player16.setPosition(Position.PIVOT);
            player16.setTeams(Collections.singletonList("Imperius Romanus"));
            playerHandler.createPlayer(player16);

            PlayerDTO player17 = new PlayerDTO();
            player17.setName("Julius");
            player17.setPosition(Position.PIVOT);
            player17.setTeams(Collections.singletonList("Imperius Romanus"));
            playerHandler.createPlayer(player17);

            PlayerDTO player18 = new PlayerDTO();
            player18.setName("Decimus");
            player18.setPosition(Position.PIVOT);
            player18.setTeams(Collections.singletonList("Imperius Romanus"));
            playerHandler.createPlayer(player18);

            PlayerDTO player19 = new PlayerDTO();
            player19.setName("Deodatos");
            player19.setPosition(Position.PIVOT);
            player19.setTeams(Collections.singletonList("Imperius Romanus"));
            playerHandler.createPlayer(player19);


            PlayerDTO player20 = new PlayerDTO();
            player20.setName("Ingvar");
            player20.setPosition(Position.DEFENDER);
            player20.setTeams(Collections.singletonList("Techno Vikings"));
            playerHandler.createPlayer(player20);

            PlayerDTO player21 = new PlayerDTO();
            player21.setName("Harald");
            player21.setPosition(Position.DEFENDER);
            player21.setTeams(Collections.singletonList("Techno Vikings"));
            playerHandler.createPlayer(player21);

            PlayerDTO player22 = new PlayerDTO();
            player22.setName("Ragnar");
            player22.setPosition(Position.PIVOT);
            player22.setTeams(Collections.singletonList("Techno Vikings"));
            playerHandler.createPlayer(player22);

            PlayerDTO player23 = new PlayerDTO();
            player23.setName("Rolfson");
            player23.setPosition(Position.RIGHT_WINGER);
            player23.setTeams(List.of("Techno Vikings", "Ancient Pharaohs"));
            playerHandler.createPlayer(player23);

            PlayerDTO player24 = new PlayerDTO();
            player24.setName("Erikson");
            player24.setPosition(Position.LEFT_WINGER);
            player24.setTeams(Collections.singletonList("Techno Vikings"));
            playerHandler.createPlayer(player24);


            PlayerDTO player25 = new PlayerDTO();
            player25.setName("Anubis");
            player25.setPosition(Position.GOALKEEPER);
            player25.setTeams(Collections.singletonList("Ancient Pharaohs"));
            playerHandler.createPlayer(player25);

            PlayerDTO player26 = new PlayerDTO();
            player26.setName("Osiris");
            player26.setPosition(Position.DEFENDER);
            player26.setTeams(Collections.singletonList("Ancient Pharaohs"));
            playerHandler.createPlayer(player26);

            PlayerDTO player27 = new PlayerDTO();
            player27.setName("Rah");
            player27.setPosition(Position.PIVOT);
            player27.setTeams(Collections.singletonList("Ancient Pharaohs"));
            playerHandler.createPlayer(player27);


            PlayerDTO player28 = new PlayerDTO();
            player28.setName("Yeti");
            player28.setPosition(Position.GOALKEEPER);
            player28.setTeams(Collections.singletonList("Arctic Boys"));
            playerHandler.createPlayer(player28);

            PlayerDTO player29 = new PlayerDTO();
            player29.setName("Kowalski");
            player29.setPosition(Position.DEFENDER);
            player29.setTeams(Collections.singletonList("Arctic Boys"));
            playerHandler.createPlayer(player29);

            PlayerDTO player30 = new PlayerDTO();
            player30.setName("Skipper");
            player30.setPosition(Position.PIVOT);
            player30.setTeams(Collections.singletonList("Arctic Boys"));
            playerHandler.createPlayer(player30);

            PlayerDTO player31 = new PlayerDTO();
            player31.setName("Rico");
            player31.setPosition(Position.RIGHT_WINGER);
            player31.setTeams(Collections.singletonList("Arctic Boys"));
            playerHandler.createPlayer(player31);

            PlayerDTO player32 = new PlayerDTO();
            player32.setName("Private");
            player32.setPosition(Position.LEFT_WINGER);
            player32.setTeams(Collections.singletonList("Arctic Boys"));
            playerHandler.createPlayer(player32);


            PlayerDTO player33 = new PlayerDTO();
            player33.setName("Paredovski");
            player33.setPosition(Position.GOALKEEPER);
            player33.setTeams(Collections.singletonList("Vodka Beavers"));
            playerHandler.createPlayer(player33);

            PlayerDTO player34 = new PlayerDTO();
            player34.setName("Centralovski");
            player34.setPosition(Position.DEFENDER);
            player34.setTeams(Collections.singletonList("Vodka Beavers"));
            playerHandler.createPlayer(player34);

            PlayerDTO player35 = new PlayerDTO();
            player35.setName("Marcagolovski");
            player35.setPosition(Position.PIVOT);
            player35.setTeams(Collections.singletonList("Vodka Beavers"));
            playerHandler.createPlayer(player35);

            PlayerDTO player36 = new PlayerDTO();
            player36.setName("Extremovski");
            player36.setPosition(Position.RIGHT_WINGER);
            player36.setTeams(Collections.singletonList("Vodka Beavers"));
            playerHandler.createPlayer(player36);

            PlayerDTO player37 = new PlayerDTO();
            player37.setName("Cruzadovski");
            player37.setPosition(Position.LEFT_WINGER);
            player37.setTeams(Collections.singletonList("Vodka Beavers"));
            playerHandler.createPlayer(player37);


            PlayerDTO player38 = new PlayerDTO();
            player38.setName("Shimazu");
            player38.setPosition(Position.GOALKEEPER);
            player38.setTeams(Collections.singletonList("Kamikaze Divers"));
            playerHandler.createPlayer(player38);

            PlayerDTO player39 = new PlayerDTO();
            player39.setName("Tokugawa");
            player39.setPosition(Position.DEFENDER);
            player39.setTeams(Collections.singletonList("Kamikaze Divers"));
            playerHandler.createPlayer(player39);

            PlayerDTO player40 = new PlayerDTO();
            player40.setName("Asakura");
            player40.setPosition(Position.PIVOT);
            player40.setTeams(Collections.singletonList("Kamikaze Divers"));
            playerHandler.createPlayer(player40);

            PlayerDTO player41 = new PlayerDTO();
            player41.setName("Uesugi");
            player41.setPosition(Position.RIGHT_WINGER);
            player41.setTeams(Collections.singletonList("Kamikaze Divers"));
            playerHandler.createPlayer(player41);

            PlayerDTO player42 = new PlayerDTO();
            player42.setName("Fujiwara");
            player42.setPosition(Position.LEFT_WINGER);
            player42.setTeams(List.of("Kamikaze Divers", "Ancient Pharaohs"));
            playerHandler.createPlayer(player42);

            List<String> tourneyTeams1 = List.of(buffRogers.getName(), blueBallers.getName(), imperiusRomanus.getName(),
            ancientPharaohs.getName(), vodkaBeavers.getName(), arcticBoys.getName(), technoVikings.getName(),
            kamikazeDivers.getName());

            List<String> tourneyTeams2 = List.of(buffRogers.getName(), imperiusRomanus.getName(), technoVikings.getName(),
            kamikazeDivers.getName());

            TournamentDto league = tournamentHandler.createTournament("Justice League", tourneyTeams1, TournamentType.POINTS);
            TournamentDto cup = tournamentHandler.createTournament("Holy Grail Cup", tourneyTeams2, TournamentType.KNOCKOUT);

            LineUpDto rogersLineup = new LineUpDto();
            rogersLineup.setTeam("Buff Rogers");
            rogersLineup.setCaptain("Vicente");
            rogersLineup.setGoalkeeper("Manuel");
            rogersLineup.setDefender("Diogo");
            rogersLineup.setRightWinger("Joel");
            rogersLineup.setLeftWinger("Arlindo");
            rogersLineup.setPivot("Vicente");

            LineUpDto ballersLineup = new LineUpDto();
            ballersLineup.setTeam("Blue Ballers");
            ballersLineup.setCaptain("Williams");
            ballersLineup.setGoalkeeper("Bellingham");
            ballersLineup.setDefender("Donavan");
            ballersLineup.setRightWinger("Dempsey");
            ballersLineup.setLeftWinger("Williams");
            ballersLineup.setPivot("Kane");

            LineUpDto romanusLineup = new LineUpDto();
            romanusLineup.setTeam("Imperius Romanus");
            romanusLineup.setCaptain("Leonius");
            romanusLineup.setGoalkeeper("Julius");
            romanusLineup.setDefender("Andronikus");
            romanusLineup.setRightWinger("Decimus");
            romanusLineup.setLeftWinger("Leonius");
            romanusLineup.setPivot("Deodatos");

            LineUpDto pharaohsLineup = new LineUpDto();
            pharaohsLineup.setTeam(ancientPharaohs.getName());
            pharaohsLineup.setCaptain("Anubis");
            pharaohsLineup.setGoalkeeper("Anubis");
            pharaohsLineup.setDefender("Osiris");
            pharaohsLineup.setRightWinger("Rolfson");
            pharaohsLineup.setLeftWinger("Fujiwara");
            pharaohsLineup.setPivot("Rah");

            LineUpDto beaversLineup = new LineUpDto();
            beaversLineup.setTeam(vodkaBeavers.getName());
            beaversLineup.setCaptain("Paredovski");
            beaversLineup.setGoalkeeper("Paredovski");
            beaversLineup.setDefender("Centralovski");
            beaversLineup.setRightWinger("Extremovski");
            beaversLineup.setLeftWinger("Cruzadovski");
            beaversLineup.setPivot("Marcagolovski");

            LineUpDto arcticLineup = new LineUpDto();
            arcticLineup.setTeam(arcticBoys.getName());
            arcticLineup.setCaptain("Skipper");
            arcticLineup.setGoalkeeper("Yeti");
            arcticLineup.setDefender("Kowalski");
            arcticLineup.setRightWinger("Rico");
            arcticLineup.setLeftWinger("Private");
            arcticLineup.setPivot("Skipper");

            LineUpDto vikingsLineup = new LineUpDto();
            vikingsLineup.setTeam(technoVikings.getName());
            vikingsLineup.setCaptain("Harald");
            vikingsLineup.setGoalkeeper("Ingvar");
            vikingsLineup.setDefender("Harald");
            vikingsLineup.setRightWinger("Rolfson");
            vikingsLineup.setLeftWinger("Erikson");
            vikingsLineup.setPivot("Ragnar");

            LineUpDto diversLineup = new LineUpDto();
            diversLineup.setTeam(kamikazeDivers.getName());
            diversLineup.setCaptain("Asakura");
            diversLineup.setGoalkeeper("Shimazu");
            diversLineup.setDefender("Tokugawa");
            diversLineup.setRightWinger("Uesugi");
            diversLineup.setLeftWinger("Fujiwara");
            diversLineup.setPivot("Asakura");

            PlaceDto place1 = new PlaceDto();
            place1.setStadium("Valhalla Field");
            place1.setDateTime(LocalDateTime.of(2025, 7, 15, 15, 45, 0));
            PlaceDto game1 = placeHandler.createPlace(place1);

            MatchDto match1 = new MatchDto();
            match1.setPlaceId(game1.getId());
            match1.setRefId(referee3.getId());
            match1.setLineUp1Id(vikingsLineup);
            match1.setLineUp2Id(rogersLineup);

            PlaceDto place2 = new PlaceDto();
            place2.setStadium("Estadio do Canguru");
            place2.setDateTime(LocalDateTime.of(2025, 7, 22, 22, 0, 0));
            PlaceDto game2 = placeHandler.createPlace(place2);

            MatchDto match2 = new MatchDto();
            match2.setPlaceId(game2.getId());
            match2.setRefId(referee4.getId());
            match2.setLineUp1Id(rogersLineup);
            match2.setLineUp2Id(vikingsLineup);

            PlaceDto place3 = new PlaceDto();
            place3.setStadium("Nova Roma");
            place3.setDateTime(LocalDateTime.of(2025, 7, 15, 22, 0, 0));
            PlaceDto game3 = placeHandler.createPlace(place3);

            MatchDto match3 = new MatchDto();
            match3.setPlaceId(game3.getId());
            match3.setRefId(referee2.getId());
            match3.setLineUp1Id(romanusLineup);
            match3.setLineUp2Id(diversLineup);

            PlaceDto place4 = new PlaceDto();
            place4.setStadium("Onigashima");
            place4.setDateTime(LocalDateTime.of(2025, 7, 22, 15, 45, 0));
            PlaceDto game4 = placeHandler.createPlace(place4);

            MatchDto match4 = new MatchDto();
            match4.setPlaceId(game4.getId());
            match4.setRefId(referee1.getId());
            match4.setLineUp1Id(diversLineup);
            match4.setLineUp2Id(romanusLineup);

            PlaceDto place5 = new PlaceDto();
            place5.setStadium("Bobr Lake");
            place5.setDateTime(LocalDateTime.of(2025, 5, 28, 10, 30, 0));
            PlaceDto game5 = placeHandler.createPlace(place5);

            MatchDto match5 = new MatchDto();
            match5.setPlaceId(game5.getId());
            match5.setRefId(referee1.getId());
            match5.setLineUp1Id(beaversLineup);
            match5.setLineUp2Id(rogersLineup);

            PlaceDto place6 = new PlaceDto();
            place6.setStadium("Estadio da Luz Azul");
            place6.setDateTime(LocalDateTime.of(2025, 5, 28, 17, 30, 0));
            PlaceDto game6 = placeHandler.createPlace(place6);

            MatchDto match6 = new MatchDto();
            match6.setPlaceId(game6.getId());
            match6.setRefId(referee4.getId());
            match6.setLineUp1Id(ballersLineup);
            match6.setLineUp2Id(diversLineup);

            PlaceDto place7 = new PlaceDto();
            place7.setStadium("Sahara Arena");
            place7.setDateTime(LocalDateTime.of(2025, 5, 30, 3, 0, 0));
            PlaceDto game7 = placeHandler.createPlace(place7);

            MatchDto match7 = new MatchDto();
            match7.setPlaceId(game7.getId());
            match7.setRefId(referee4.getId());
            match7.setLineUp1Id(pharaohsLineup);
            match7.setLineUp2Id(romanusLineup);

            PlaceDto place8 = new PlaceDto();
            place8.setStadium("Antartida");
            place8.setDateTime(LocalDateTime.of(2025, 5, 30, 7, 0, 0));
            PlaceDto game8 = placeHandler.createPlace(place8);

            MatchDto match8 = new MatchDto();
            match8.setPlaceId(game8.getId());
            match8.setRefId(referee3.getId());
            match8.setLineUp1Id(arcticLineup);
            match8.setLineUp2Id(vikingsLineup);

            PlaceDto place9 = new PlaceDto();
            place9.setStadium("Bobr Lake");
            place9.setDateTime(LocalDateTime.of(2025, 6, 1, 13, 0, 0));
            PlaceDto game9 = placeHandler.createPlace(place9);

            MatchDto match9 = new MatchDto();
            match9.setPlaceId(game9.getId());
            match9.setRefId(referee1.getId());
            match9.setLineUp1Id(beaversLineup);
            match9.setLineUp2Id(pharaohsLineup);

            PlaceDto place10 = new PlaceDto();
            place10.setStadium("Onigashima");
            place10.setDateTime(LocalDateTime.of(2025, 6, 1, 21, 0, 0));
            PlaceDto game10 = placeHandler.createPlace(place10);

            MatchDto match10 = new MatchDto();
            match10.setPlaceId(game10.getId());
            match10.setRefId(referee2.getId());
            match10.setLineUp1Id(diversLineup);
            match10.setLineUp2Id(vikingsLineup);

            PlaceDto place11 = new PlaceDto();
            place11.setStadium("Antartida");
            place11.setDateTime(LocalDateTime.of(2025, 6, 3, 18, 0, 0));
            PlaceDto game11 = placeHandler.createPlace(place11);

            MatchDto match11 = new MatchDto();
            match11.setPlaceId(game11.getId());
            match11.setRefId(referee3.getId());
            match11.setLineUp1Id(arcticLineup);
            match11.setLineUp2Id(ballersLineup);

            PlaceDto place12 = new PlaceDto();
            place12.setStadium("Nova Roma");
            place12.setDateTime(LocalDateTime.of(2025, 6, 1, 2, 0, 0));
            PlaceDto game12 = placeHandler.createPlace(place12);

            MatchDto match12 = new MatchDto();
            match12.setPlaceId(game12.getId());
            match12.setRefId(referee1.getId());
            match12.setLineUp1Id(romanusLineup);
            match12.setLineUp2Id(rogersLineup);

            matchHandler.createMatch(match1);
            matchHandler.createMatch(match2);
            matchHandler.createMatch(match3);
            matchHandler.createMatch(match4);
            matchHandler.createMatch(match5);
            matchHandler.createMatch(match6);
            matchHandler.createMatch(match7);
            matchHandler.createMatch(match8);
            matchHandler.createMatch(match9);
            matchHandler.createMatch(match10);
            matchHandler.createMatch(match11);
            matchHandler.createMatch(match12);

            tournamentHandler.addScheduledMatch(cup.getId(), 3l);
            tournamentHandler.addScheduledMatch(cup.getId(), 4l);
            tournamentHandler.addScheduledMatch(cup.getId(), 5l);
            tournamentHandler.addScheduledMatch(cup.getId(), 6l);

            tournamentHandler.addScheduledMatch(league.getId(), 7l);
            tournamentHandler.addScheduledMatch(league.getId(), 8l);
            tournamentHandler.addScheduledMatch(league.getId(), 9l);
            tournamentHandler.addScheduledMatch(league.getId(), 10l);
            tournamentHandler.addScheduledMatch(league.getId(), 11l);
            tournamentHandler.addScheduledMatch(league.getId(), 12l);
            tournamentHandler.addScheduledMatch(league.getId(), 13l);
            tournamentHandler.addScheduledMatch(league.getId(), 14l);


            System.out.println("Sanity tests completed successfully");
        };
    }
}
