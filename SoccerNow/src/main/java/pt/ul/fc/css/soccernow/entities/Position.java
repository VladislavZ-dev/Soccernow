package pt.ul.fc.css.soccernow.entities;

public enum Position {
    GOALKEEPER, DEFENDER, LEFT_WINGER, RIGHT_WINGER, PIVOT;

    public static String toString(Position position) {
        switch(position) {
            case GOALKEEPER : return "goalkeeper";
            case DEFENDER : return "defender";
            case LEFT_WINGER : return "left winger";
            case RIGHT_WINGER : return "right winger";
            case PIVOT : return "pivot";
            default : return "";
        }
    }
}