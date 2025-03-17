package me.hapyl.fight.game.heroes.doctor;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

import javax.annotation.Nonnull;

public class DrEdData extends PlayerData implements Ticking {

    private final DrEd hero;

    protected BlockShield shield;

    public DrEdData(@Nonnull DrEd hero, @Nonnull GamePlayer player) {
        super(player);

        this.hero = hero;
    }

    @Override
    public void tick() {
        if (shield == null) {
            return;
        }

        final boolean hasDamaged = shield.update();

        if (hasDamaged) {
            remove();
            hero.scheduleNextShield(player, true);
        }
    }

    @Override
    public void remove() {
        if (shield != null) {
            shield.remove();
            shield = null;
        }
    }

    public void newShield(boolean isHit) {
        final int cooldown = hero.getPassiveTalent().cooldown;

        player.schedule(
                () -> {
                    remove();
                    shield = new BlockShield(player);
                }, isHit ? cooldown : cooldown / 2
        );
    }
}
