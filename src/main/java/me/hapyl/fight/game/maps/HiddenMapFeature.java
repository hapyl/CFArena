package me.hapyl.fight.game.maps;

public abstract class HiddenMapFeature extends MapFeature {
    public HiddenMapFeature() {
        super("", "");
    }

    @Deprecated
    public HiddenMapFeature(String name, String info) {
        this();
    }
}
