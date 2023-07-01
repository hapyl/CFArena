package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.event.Listener;

public abstract class ContrailCosmetic extends Cosmetic implements Listener {

    public ContrailCosmetic(String name, String description, Rarity rarity) {
        super(name, description, Type.CONTRAIL, rarity);
    }

    public abstract void onMove(Display display);

    @Override
    public final void onDisplay(Display display) {
        onMove(display);
    }
}
