package me.hapyl.fight.game.heroes.troll;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;

public class TrollData extends PlayerData {

    private StickyCobweb cobweb;

    public TrollData(@Nonnull GamePlayer player) {
        super(player);
    }

    @Override
    public void remove() {
        if (cobweb != null) {
            cobweb.remove();
            cobweb = null;
        }
    }

    public void createCobweb() {
        remove();
        cobweb = new StickyCobweb(this, player);
    }

    public void clearCobweb(@Nonnull GamePlayer player, @Nonnull Block block) {
        if (this.cobweb == null) {
            return;
        }

        if (this.player.isSelfOrTeammate(player)) {
            //return;
        }

        this.cobweb.clear(block);
    }
}
