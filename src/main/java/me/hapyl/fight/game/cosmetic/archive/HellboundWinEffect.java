package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import org.bukkit.Material;

public class HellboundWinEffect extends Cosmetic {
    public HellboundWinEffect() {
        super("Hellbound", """
                It was bound to happen.
                """, Type.WIN, Rarity.CURSED, Material.NETHER_BRICK);
    }

    @Override
    protected void onDisplay(Display display) {

    }
}
