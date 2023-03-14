package me.hapyl.fight.game.heroes.dvar;

public enum DVarApplicable {

    ROOT(""), // root
    ARCHER("archer"),
    ;

    private final String path;

    DVarApplicable(String path) {
        this.path = path;
    }

    public boolean isRoot() {
        return this == ROOT;
    }

    public String getPath() {
        return path;
    }
}
