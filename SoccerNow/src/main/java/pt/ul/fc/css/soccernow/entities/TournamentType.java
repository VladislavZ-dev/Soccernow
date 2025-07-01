package pt.ul.fc.css.soccernow.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TournamentType {
    KNOCKOUT, POINTS;

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @JsonCreator
    public static TournamentType fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return TournamentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle invalid enum values gracefully
            throw new IllegalArgumentException("Invalid tournament type: " + value + 
                ". Valid values are: KNOCKOUT, POINTS");
        }
    }

    public static String toString(TournamentType type) {
        switch(type) {
            case KNOCKOUT : return "Knockout";
            case POINTS : return "Points";
            default : return "";
        }
    }
}