package me.hapyl.fight.game.heroes.shaman;

import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

public class ShamanData extends PlayerData {

    private final double maxOverheal = 100;
    private double overheal;

    public ShamanData(GamePlayer player) {
        super(player);
    }

    public void increaseOverheal(double amount) {
        overheal = Math.min(overheal + amount, maxOverheal);

        player.spawnBuffDisplay("%s &a+%.0f".formatted(Named.OVERHEAL.getCharacter(), amount), 15);
    }

    public void decreaseOverheal(double amount) {
        overheal = Math.max(overheal - amount, 0);

        player.spawnDebuffDisplay("%s &c-%.0f".formatted(Named.OVERHEAL.getCharacter(), amount), 15);
    }

    public double getOverheal() {
        return overheal;
    }

    @Override
    public void remove() {
        overheal = 0;
    }

    public boolean isOverheadMaxed() {
        return overheal == maxOverheal;
    }
}
