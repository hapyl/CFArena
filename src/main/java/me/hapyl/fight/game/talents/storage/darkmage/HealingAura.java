package me.hapyl.fight.game.talents.storage.darkmage;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.HeroHandle;
import me.hapyl.fight.game.heroes.storage.extra.DarkMageSpell;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HealingAura extends DarkMageTalent {

    public HealingAura() {
        super("Healing Aura", "Creates a healing circle at your location that heals all players periodically.", Material.APPLE);
        this.setCdSec(30);
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton first() {
        return DarkMageSpell.SpellButton.LEFT;
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton second() {
        return DarkMageSpell.SpellButton.LEFT;
    }

    @Override
    public Response execute(Player player) {
        if (HeroHandle.DARK_MAGE.isUsingUltimate(player)) {
            return Response.error("Unable to use while in ultimate form!");
        }

        final double radius = 2.5d;
        final Location location = player.getLocation();

        final int delay = 1;

        new GameTask() {
            private int tick = 200;
            private double theta = 0;

            @Override
            public void run() {
                final double x = radius * Math.sin(theta);
                final double z = radius * Math.cos(theta);

                location.add(x, 0, z);
                PlayerLib.spawnParticle(location, Particle.VILLAGER_HAPPY, 2, 0.01, 0, 0.01, 0);
                location.subtract(x, 0, z);

                theta = theta >= 36 ? 0 : theta + 0.1;

                if ((tick % 20) == 0) {
                    Utils.getPlayersInRange(location, radius).forEach(target -> {
                        GamePlayer.getPlayer(target).heal(2.0d);
                        PlayerLib.playSound(target, Sound.BLOCK_GRASS_HIT, 1.0f);
                    });
                    PlayerLib.spawnParticle(location, Particle.HEART, 5, 1, 0.2, 1, 0.01f);
                }

                if ((tick -= delay) <= 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, delay);

        return Response.OK;
    }
}
