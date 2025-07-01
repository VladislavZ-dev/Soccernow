    package pt.ul.fc.css.soccernow.dto;

    import java.util.*;

    import jakarta.persistence.*;
import pt.ul.fc.css.soccernow.entities.TournamentType;

    public class TournamentDto{

        private long id;
        private String name;
        private TournamentType type;
        private List<String> teamsNames;
        private List<Long> scheduledMatchesIds;
        private List<Long> playedMatchesIds;
        private Map<String, String> teamsPlacements;

        
        public TournamentDto() {
            this.teamsNames = new ArrayList<>();
            this.scheduledMatchesIds = new ArrayList<>();
            this.playedMatchesIds = new ArrayList<>();
            this.teamsPlacements = new HashMap<>();
        }

        public TournamentDto(long id, String name, TournamentType type, List<Long> playedMatches, List<Long> scheduledMatches, List<String> teams, Map<String, String> teamsPlacements) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.playedMatchesIds = playedMatches;
            this.scheduledMatchesIds = scheduledMatches;
            this.teamsNames = teams;
            this.teamsPlacements = teamsPlacements;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTeamsNames() {
            return teamsNames;
        }

        public void setTeamsNames(List<String> teamsNames) {
            this.teamsNames = teamsNames;
        }

        public List<Long> getScheduledMatchesIds() {
            return scheduledMatchesIds;
        }

        public void setScheduledMatchesIds(List<Long> scheduledMatchesIds) {
            this.scheduledMatchesIds = scheduledMatchesIds;
        }

        public List<Long> getPlayedMatchesIds() {
            return playedMatchesIds;
        }

        public void setPlayedMatchesIds(List<Long> playedMatchesIds) {
            this.playedMatchesIds = playedMatchesIds;
        }

        public Map<String, String> getTeamsPlacements() {
            return teamsPlacements;
        }

        public void setTeamsPlacements(Map<String, String> teamsPlacements) {
            this.teamsPlacements = teamsPlacements;
        }

        public TournamentType getType() {
            return type;
        }

        public void setType(TournamentType type) {
            this.type = type;
        }
    }