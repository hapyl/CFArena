package me.hapyl.fight.game.crate;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;
import java.util.Random;

// Should probably make this a whole class rather than an interface
public interface CrateAnimation {

    CrateAnimation DEFAULT = (loot, chest) -> {

        final ArmorStand armorStand = Entities.ARMOR_STAND_MARKER.spawn(chest.subtractAsNew(0.0d, 1.0d, 0.0d), self -> {
            self.setInvisible(true);

            CFUtils.setEquipment(self, equipment -> {
                equipment.setHelmet(ItemBuilder.of(loot.getEnumCrate().getCrate().getMaterial()).asIcon());
            });
        });

        final Sound[] sounds;

        if (new Random().nextFloat() >= 0.9f) {
            sounds = randomSound();
        }
        else {
            sounds = new Sound[] {
                    Sound.BLOCK_NOTE_BLOCK_BIT,
                    Sound.BLOCK_NOTE_BLOCK_BASEDRUM,
                    Sound.BLOCK_WOOD_BREAK,
                    Sound.BLOCK_WOOD_PLACE
            };
        }

        return new CrateTask(loot, chest) {
            @Override
            public void tick(int tick) {
                final Location location = armorStand.getLocation();
                final int range = getRange(0);

                location.add(0.0d, 1.5 / range, 0.0d);
                location.setYaw(location.getYaw() + (float) 360 * 2 / range);

                armorStand.teleport(location);
            }
        }.tick(0, 60, ref -> {
            final Location location = armorStand.getLocation().add(0.0d, 1.5d, 0.0d);
            final int tick = ref.getTick();
            final int range = ref.getRange(0);

            // Fx
            for (Sound sound : sounds) {
                PlayerLib.playSound(location, sound, 0.5f + tick * 1.5f / range);
            }

            PlayerLib.spawnParticle(location, Particle.FIREWORK, 1, 0.2d, 0.2d, 0.2d, 0.1f);
        }).tick(60, ref -> {
            final Location chestLocation = armorStand.getLocation().add(0.0d, 1.5d, 0.0d);

            ref.display(chestLocation);
            armorStand.remove();

            // Fx
            PlayerLib.spawnParticle(chestLocation, Particle.FLASH, 1);
            PlayerLib.spawnParticle(chestLocation, Particle.POOF, 5, 0.1d, 0.1d, 0.1d, 0.05f);

        }).tick(100, ref -> PlayerLib.playSound(armorStand.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.75f));
    };

    static Sound[] randomSound() {
        final Sound sound = CollectionUtils.randomElement(Sound.values());

        if (sound == null) {
            return randomSound();
        }

        final String name = sound.name().toLowerCase();
        if (name.contains("music")) {
            return randomSound();
        }

        return new Sound[] { sound };
    }

    @Nonnull
    CrateTask play(@Nonnull CrateLoot loot, @Nonnull CrateLocation chest);

    default void play0(@Nonnull CrateLoot loot, @Nonnull CrateLocation chest) {
        chest.hologram.destroy();
        chest.playOpenAnimation();

        loot.getPlayer().closeInventory();
        play(loot, chest);
    }

    @Nonnull
    static CrateAnimation byRarity(@Nonnull Rarity rarity) {
        return DEFAULT;
    }

}
