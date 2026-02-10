package me.hapyl.fight.game.heroes.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.alchemist.AlchemistPotion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemistData extends PlayerData {

    protected AlchemistState state;
    @Nullable protected ActivePotion activePotion;
    protected double toxin;

    public AlchemistData(GamePlayer player) {
        super(player);

        this.state = AlchemistState.NORMAL;
        this.activePotion = null;
        this.toxin = 0.0d;
    }

    @Override
    public void remove() {
        cancelActivePotion();
        toxin = 0.0d;
    }

    public void setActivePotion(@Nonnull GamePlayer player, @Nonnull AlchemistPotion potion) {
        cancelActivePotion();

        activePotion = potion.use(this, player);
        toxin += potion.intoxication();

        // Fx
    }

    public void cancelActivePotion() {
        if (activePotion != null) {
            activePotion.cancel();
            activePotion = null;
        }
    }
}
