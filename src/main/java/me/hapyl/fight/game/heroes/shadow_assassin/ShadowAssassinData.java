package me.hapyl.fight.game.heroes.shadow_assassin;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import javax.annotation.Nonnull;

public class ShadowAssassinData extends PlayerData {

    public static final int MAX_ENERGY = 100;

    private final ShadowAssassin hero;

    private AssassinMode mode;
    private int energy;

    public ShadowAssassinData(@Nonnull GamePlayer player, @Nonnull ShadowAssassin hero) {
        super(player);

        this.hero = hero;
        this.mode = AssassinMode.STEALTH;
        this.energy = MAX_ENERGY / 2;
    }

    public void switchMode(@Nonnull AssassinMode newMode) {
        if (mode == newMode) {
            player.sendMessage("&cAlready in this mode!");
            return;
        }

        mode = newMode;
        mode.switchTo(player, hero);

        // Damage & Fx
        Collect.nearbyEntities(player, 1.5d).forEach(entity -> {
            if (entity.equals(player)) {
                return;
            }

            entity.damage(1, player);
        });

        playSwitchFx();
    }

    @Nonnull
    public AssassinMode getMode() {
        return mode;
    }

    public int getEnergy() {
        return energy;
    }

    public void subtractEnergy(int furyCost) {
        energy = Math.max(energy - furyCost, 1);
    }

    public void addEnergy(int energyRegen) {
        energy = Math.min(energy + energyRegen, MAX_ENERGY);
    }

    @Override
    public void remove() {

    }

    private void playSwitchFx() {
        final Location location = player.getLocation().add(0.0d, 1.0d, 0.0d);
        final World world = player.getWorld();
        final double radius = 1.25d;
        final double rings = 15;

        for (double d = 0.0d; d < Math.PI; d += Math.PI / rings) {
            final double rad = Math.sin(d) * radius;
            final double z = Math.cos(d) * radius;

            new GameTask() {
                private double j;

                @Override
                public void run() {
                    for (int i = 0; i < 4; i++) {
                        next();
                    }
                }

                private void next() {
                    if (j >= Math.PI * 2) {
                        cancel();
                        return;
                    }

                    final double y = rad * Math.sin(j);
                    final double x = rad * Math.cos(j);

                    location.add(x, y, z);
                    world.spawnParticle(Particle.DUST_COLOR_TRANSITION, location, 1, 0, 0, 0, mode.getTransition());
                    location.subtract(x, y, z);

                    j += Math.PI / (rings / 2);
                }
            }.runTaskTimer(0, 1);
        }

        player.playWorldSound(Sound.ENTITY_WITCH_DRINK, 0.0f);
        player.playWorldSound(Sound.ENTITY_ENDER_DRAGON_HURT, 0.0f);
        player.playWorldSound(Sound.ENTITY_SHULKER_HURT, 0.0f);
    }
}
