package me.hapyl.fight.game.cosmetic.storage;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Material;

public class ElectrocuteCosmetic extends Cosmetic {
    public ElectrocuteCosmetic() {
        super("Electrocute", "Bzz~t.", 1000, Type.DEATH, Rarity.UNCOMMON, Material.LIGHT_BLUE_STAINED_GLASS);
    }

    @Override
    public void onDisplay(Display display) {
        GameTask.runTaskTimerTimes((task, tick) -> {
            display.getWorld().strikeLightningEffect(display.getLocation().add(0.0d, 2.0d, 0.0d));
        }, 10, 2, 10);
    }
}
