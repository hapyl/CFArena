package me.hapyl.fight.game.maps;

public abstract class HiddenLevelFeature extends LevelFeature {
    public HiddenLevelFeature() {
        super("Hidden Feature", "Hidden Feature");
    }

    @Deprecated
    public HiddenLevelFeature(String name, String info) {
        this();
    }
}
