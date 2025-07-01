package pt.ul.fc.css.soccernow.presentation.thymeleaf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ul.fc.css.soccernow.controllers.MatchController;
import pt.ul.fc.css.soccernow.controllers.PlayerController;
import pt.ul.fc.css.soccernow.controllers.RefereeController;
import pt.ul.fc.css.soccernow.controllers.TeamController;
import pt.ul.fc.css.soccernow.controllers.TournamentController;
import pt.ul.fc.css.soccernow.dto.CardRequestDto;
import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.dto.PlayerDTO;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.dto.ScoreRequestDto;
import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.dto.TournamentDto;

@Controller
public class webUIController {

    @Autowired
    private PlayerController playerController;

    @Autowired
    private TeamController teamController;

    @Autowired
    private MatchController matchController;

    @Autowired
    private RefereeController refereeController;

    @Autowired
    private TournamentController tournamentController;

    private List<MatchDto> matches;

    private List<RefereeDTO> referees;

    @GetMapping({"/"})
    public String load() {
        return "login";
    }

    @GetMapping({"/dashboard"})
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping({"/player-search-with-filter"})
    public String playerSearch(@RequestParam(required = false) String name,
     @RequestParam(required = false) String position,
     @RequestParam(required = false) Integer numGoals,
     @RequestParam(required = false) Integer numCards,
     @RequestParam(required = false) Integer numGames, Model model) {
        if (name == null)
            return "player-search";
        List<PlayerDTO> filteredPlayers = playerController.searchWithFilters(name, position, numGoals, numCards, numGames).getBody();

        Map<String, Integer> goalsPerPlayerMap = new HashMap<>();
        Map<String, Long> cardsPerPlayerMap = new HashMap<>();
        Map<String, Integer> matchesPerPlayerMap = new HashMap<>();
        for (PlayerDTO player : filteredPlayers) {
            int goals = matchController.getNumberGoalsOfPlayer(player.getName()).getBody();
            goalsPerPlayerMap.put(player.getName(), goals);
            long cards = matchController.countYellowCardsByPlayerId(player.getId());
            cards += matchController.countRedCardsByPlayerId(player.getId());
            cardsPerPlayerMap.put(player.getName(), cards);
            int matches = matchController.countMatchesByPlayer(player.getId());
            matchesPerPlayerMap.put(player.getName(), matches);
        }

        model.addAttribute("players", filteredPlayers);
        model.addAttribute("goalsMap", goalsPerPlayerMap);
        model.addAttribute("cardsMap", cardsPerPlayerMap);
        model.addAttribute("matchesMap", matchesPerPlayerMap);
        return "player-search";
    }

    @GetMapping({"/team-search-with-filter"})
    public String teamSearch(@RequestParam(required = false) String name,
     @RequestParam(required = false) Integer numPlayers,
     @RequestParam(required = false) Integer numWins,
     @RequestParam(required = false) Integer numDraws,
     @RequestParam(required = false) Integer numLosses,
     @RequestParam(required = false) String trophy,
     @RequestParam(required = false) String missingPosition, Model model) {
        if (name == null) 
            return "team-search";
        List<TeamDto> filteredTeams = teamController.searchWithFilters(name, numPlayers, numWins, numDraws, numLosses, trophy, 
         missingPosition).getBody();

        Map<String, Integer> playersPerTeamMap = new HashMap<>();
        Map<String, Integer> winsPerTeamMap = new HashMap<>();
        Map<String, Integer> drawsPerTeamMap = new HashMap<>();
        Map<String, Integer> lossesPerTeamMap = new HashMap<>();
        for (TeamDto team : filteredTeams) {
            int numPlayersInTeam = teamController.getNumberOfPlayers(team.getName());
            playersPerTeamMap.put(team.getName(), numPlayersInTeam);
            int numWinsOfTeam = teamController.countWinsOfTeam(team.getName());
            winsPerTeamMap.put(team.getName(), numWinsOfTeam);
            int numDrawsOfTeam = teamController.countDrawsOfTeam(team.getName());
            drawsPerTeamMap.put(team.getName(), numDrawsOfTeam);
            int numLossesOfTeam = teamController.countLossesOfTeam(team.getName());
            lossesPerTeamMap.put(team.getName(), numLossesOfTeam);
        }

        model.addAttribute("teams", filteredTeams);
        model.addAttribute("playersMap", playersPerTeamMap);
        model.addAttribute("winsMap", winsPerTeamMap);
        model.addAttribute("drawsMap", drawsPerTeamMap);
        model.addAttribute("lossesMap", lossesPerTeamMap);
        return "team-search";
    }

    @GetMapping({"/tournament-search-with-filter"})
    public String tournamentSearch(@RequestParam(required = false) String name,
     @RequestParam(required = false) String team,
     @RequestParam(required = false) Integer toPlayGames,
     @RequestParam(required = false) Integer playedGames, Model model) {
        if (name == null) 
            return "tournament-search";
        List<TournamentDto> filteredTournaments = tournamentController.searchWithFilters(name, team, toPlayGames, playedGames).getBody();

        Map<Long, Integer> playedMatchesNumMap = new HashMap<>();
        Map<Long, Integer> toPlayMatchesNumMap = new HashMap<>();

        for (TournamentDto tournament : filteredTournaments) {
            playedMatchesNumMap.put(tournament.getId(), tournament.getPlayedMatchesIds().size());
            toPlayMatchesNumMap.put(tournament.getId(), tournament.getScheduledMatchesIds().size());
        }

        model.addAttribute("tournaments", filteredTournaments);
        model.addAttribute("played", playedMatchesNumMap);
        model.addAttribute("toPlay", toPlayMatchesNumMap);
        return "tournament-search";
    }

    @GetMapping({"/match-search-with-filter"})
    public String matchSearch(@RequestParam(required = false) String matchPlanning,
     @RequestParam(required = false) Integer matchGoals,
     @RequestParam(required = false) String matchLocation,
     @RequestParam(required = false) String matchTime, Model model) {
        List<MatchDto> filteredMatches = matchController.searchWithFilters(matchPlanning, matchGoals, matchLocation, matchTime).getBody();

        populateMatchInfo(model, filteredMatches);
        model.addAttribute("matches", filteredMatches);
        this.matches = filteredMatches;

        model.addAttribute("referees", this.referees);
        populateRefereeInfo(model, this.referees);
        
        return "register-match";
    }

    private void populateMatchInfo(Model model, List<MatchDto> matches) {
        if (matches == null) {
            return;
        }
        Map<Long, String> matchPlanningMap = new HashMap<>();
        Map<Long, Integer> matchGoalsMap = new HashMap<>();
        Map<Long, String> matchLocationMap = new HashMap<>();
        Map<Long, String> matchTimeMap = new HashMap<>();

        for (MatchDto match : matches) {
            matchPlanningMap.put(match.getId(), matchController.getMatchPlanning(match));
            matchGoalsMap.put(match.getId(), matchController.getMatchGoals(match));
            matchLocationMap.put(match.getId(), matchController.getMatchLocation(match));
            matchTimeMap.put(match.getId(), matchController.getMatchTimeOfDay(match));
        }

        model.addAttribute("matchPlanningMap", matchPlanningMap);
        model.addAttribute("matchGoalsMap", matchGoalsMap);
        model.addAttribute("matchLocationMap", matchLocationMap);
        model.addAttribute("matchTimeMap", matchTimeMap);
    }

    @GetMapping({"/referee-search-with-filter"})
    public String refereeSearch(@RequestParam(required = false) String refeName, 
     @RequestParam(required = false) Integer matchesRefereed,
     @RequestParam(required = false) Integer cardsShown, Model model) {
        List<RefereeDTO> filteredReferees = refereeController.searchWithFilters(refeName, matchesRefereed, cardsShown).getBody();

        model.addAttribute("referees", filteredReferees);
        populateRefereeInfo(model, filteredReferees);
        this.referees = filteredReferees;

        model.addAttribute("matches", this.matches);
        populateMatchInfo(model, this.matches);

        return "register-match";
    }

    private void populateRefereeInfo(Model model, List<RefereeDTO> referees) {
        if (referees == null)
            return;
        Map<String, Integer> gamesPerRefMap = new HashMap<>();
        Map<String, Integer> cardsPerRefMap = new HashMap<>();

        for (RefereeDTO referee : referees) {
            int matchesNum = matchController.getNumberOfGamesRefereedByReferee(referee.getId());
            gamesPerRefMap.put(referee.getName(), matchesNum);
            int cards = matchController.getNumberOfCardsIssuedByReferee(referee.getId());
            cardsPerRefMap.put(referee.getName(), cards);
        }

        model.addAttribute("numMatchesMap", gamesPerRefMap);
        model.addAttribute("numCardsMap", cardsPerRefMap);
    }

    @GetMapping({"/register-match-page"})
    public String registerMatchPage() {
        return "register-match";
    }

    @GetMapping({"/register-match"})
    public String registerMatch(@RequestParam Long matchId,
     @RequestParam List<String> players,
     @RequestParam List<Integer> goals, 
     @RequestParam List<String> cards, 
     @RequestParam String button, Model model) {
        Map<Long, Integer> playerGoals = new HashMap<>();
        List<CardRequestDto> playerCards = new LinkedList<>();
        for (int i = 0; i < players.size(); i++) {
            int goalsScored = goals.get(i);
            if (goalsScored > 0) {
                Long playerId = playerController.getplayerByName(players.get(i)).getBody().getId();
                playerGoals.put(playerId, goalsScored);
            }
            
            String card = cards.get(i);
            if (!card.equals("")) {
                CardRequestDto playerCard = new CardRequestDto(matchId, players.get(i), card.equals("red"));
                playerCards.add(playerCard);
            }
        }
        ScoreRequestDto score = new ScoreRequestDto(matchId, playerGoals);
        matchController.registerScore(score);
        
        for (CardRequestDto playerCard : playerCards) 
            matchController.registerCard(playerCard);

        if (button.equals("mark"))
            tournamentController.markMatchAsPlayed(matchController.getMatchById(matchId).getBody().getTournamentId(), matchId);

        model.addAttribute("matches", this.matches);
        populateMatchInfo(model, this.matches);
        model.addAttribute("referees", this.referees);
        populateRefereeInfo(model, this.referees);
        return "register-match";
    }

    @GetMapping({"/fetch-match-with-id"})
    public String fetchMatchWithId(@RequestParam(required = false) Long matchId, Model model) {
        model.addAttribute("matchId", matchId);
        if (matchId != null) {
            ResponseEntity<MatchDto> response = matchController.getMatchById(matchId);
            if (response.getStatusCode() == HttpStatusCode.valueOf(200)) {

                Long tournamentId = matchController.getMatchById(matchId).getBody().getTournamentId();
                model.addAttribute("tournamentId", tournamentId);

                MatchDto match = response.getBody();

                List<String> lineUp1 = new LinkedList<>();
                lineUp1.add(match.getLineUp1Id().getGoalkeeper());
                lineUp1.add(match.getLineUp1Id().getDefender());
                lineUp1.add(match.getLineUp1Id().getPivot());
                lineUp1.add(match.getLineUp1Id().getRightWinger());
                lineUp1.add(match.getLineUp1Id().getLeftWinger());

                List<String> lineUp2 = new LinkedList<>();
                lineUp2.add(match.getLineUp2Id().getGoalkeeper());
                lineUp2.add(match.getLineUp2Id().getDefender());
                lineUp2.add(match.getLineUp2Id().getPivot());
                lineUp2.add(match.getLineUp2Id().getRightWinger());
                lineUp2.add(match.getLineUp2Id().getLeftWinger());

                model.addAttribute("team1", match.getLineUp1Id().getTeam());
                model.addAttribute("lineUp1", lineUp1);
                model.addAttribute("team2", match.getLineUp2Id().getTeam());
                model.addAttribute("lineUp2", lineUp2);
            }
            else {
                model.addAttribute("matchId", null);
                model.addAttribute("tournamentId", null);
            } 
        }
        model.addAttribute("matches", this.matches);
        populateMatchInfo(model, this.matches);
        model.addAttribute("referees", this.referees);
        populateRefereeInfo(model, this.referees);

        
        return "register-match";
    }

    @GetMapping({"/clear-outputs"})
    public String clearMatches(Model model) {
        this.matches = null;
        model.addAttribute("matches", null);
        this.referees = null;
        model.addAttribute("referees", null);
        return "register-match";
    }
}
