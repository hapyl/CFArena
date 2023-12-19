package me.hapyl.fight.game.talents.archive.techie;

import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.PlayerGameTask;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public interface DeviceHack {

    ItemStack DEVICE_ITEM = new ItemStack(Material.IRON_TRAPDOOR);

    void onHack(@Nonnull GamePlayer player);

    default void onTick(@Nonnull GamePlayer player, int tick) {
    }

    int getCastingTime();

    default void startDevice(@Nonnull GamePlayer player) {
        final int castingTime = getCastingTime();
        final Location location = player.getLocation();
        location.add(location.getDirection().normalize().multiply(1.5d)).subtract(0, 0.5, 0);

        final Vector direction = player.getEyeLocation().toVector().subtract(location.toVector());
        location.setDirection(direction);

        player.addEffect(GameEffectType.MOVEMENT_CONTAINMENT, castingTime, true);

        final ArmorStand device = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setSilent(true);
            self.setHelmet(DEVICE_ITEM);
            self.setInvisible(true);
        });

        new PlayerGameTask(player) {
            private final double anglePerTick = 60.0d / castingTime;
            private int tick = 0;

            @Override
            public void run() {
                if (tick > castingTime) {
                    onHack(player);
                    device.remove();
                    cancel();

                    // Fx
                    player.playSound(Sound.ENTITY_ENDERMAN_HURT, 0.75f);
                    player.playSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.75f);
                    return;
                }

                final int letters = (int) (5d / castingTime * tick);
                final String hackProgress = "&f&l&k₪ ".repeat(letters) + "&f&l_ ".repeat(5 - letters);

                player.sendSubtitle(hackProgress, 0, 10, 0);

                if (tick % 2 == 0) {
                    player.playWorldSound(Sound.BLOCK_LEVER_CLICK, 0.5f + (0.2f / castingTime * tick));
                }

                // Fx
                device.setHeadPose(new EulerAngle(Math.toRadians(anglePerTick * tick), 0, 0));

                onTick(player, tick);

                // Tick
                tick++;
            }

            @Override
            public void onTaskStopBecauseOfDeath() {
                device.remove();
            }
        }.runTaskTimer(0, 1);
    }

}
