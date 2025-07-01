package pt.ul.fc.css.soccernow.entities;

public enum Placement {
    FIRST("1st"),
    SECOND("2nd"), 
    THIRD("3rd"),
    PARTICIPANT("Participant");

    private final String displayName;

    Placement(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String toString(Placement placement) {
        return placement != null ? placement.displayName : "Participant";
    }

    public static Placement fromString(String str) {
        if (str == null) return PARTICIPANT;
        
        switch (str.toLowerCase()) {
            case "1st":
            case "first":
                return FIRST;
            case "2nd": 
            case "second":
                return SECOND;
            case "3rd":
            case "third":
                return THIRD;
            case "participant":
            default:
                return PARTICIPANT;
        }
    }
}