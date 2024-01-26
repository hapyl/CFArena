package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import java.util.Random;

public class BonesAndShredsCosmetic extends Cosmetic {

    private final Random random;

    public BonesAndShredsCosmetic() {
        super("Shreds and Bones", "*Explodes*", Type.DEATH, Rarity.UNCOMMON, Material.BONE);

        this.random = new Random();
    }

    @Override
    protected void onDisplay(Display display) {
        for (int i = 0; i < 20; i++) {
            final Item item = display.item(Material.BONE, 20);

            item.setVelocity(new Vector(randomDouble(), randomDouble(), randomDouble()));
        }

        // Fx
        display.sound(Sound.ENTITY_SKELETON_DEATH, 0.0f);
        display.sound(Sound.ENTITY_SKELETON_DEATH, 1.25f);
    }

    private double randomDouble() {
        final double v = random.nextDouble(0.15d, 0.5d);
        return random.nextBoolean() ? v : -v;
    }
}
