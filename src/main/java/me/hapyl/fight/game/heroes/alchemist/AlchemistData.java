package me.hapyl.fight.game.heroes.alchemist;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.alchemist.AlchemistPotion;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class AlchemistData extends PlayerData {

    public AlchemistState state;
    public ActivePotion activePotion;

    public AlchemistData(GamePlayer player) {
        super(player);

        this.state = AlchemistState.NORMAL;
        this.activePotion = null;
    }

    @Override
    public void remove() {
        cancelActivePotion();
    }

    public void setActivePotion(@Nonnull GamePlayer player, @Nonnull AlchemistPotion potion) {
        cancelActivePotion();

        activePotion = potion.use(this, player);
    }

    public void cancelActivePotion() {
        if (activePotion != null) {
            activePotion.cancel();
            activePotion = null;
        }
    }
}
