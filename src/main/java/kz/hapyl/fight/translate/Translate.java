package kz.hapyl.fight.translate;

public enum Translate {

    NO_PERMISSIONS("&cNo permissions."),


    ;

    private final String string;

    Translate(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

}
