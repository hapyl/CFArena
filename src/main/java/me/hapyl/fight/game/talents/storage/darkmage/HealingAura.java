package me.hapyl.fight.game.talents.storage.darkmage;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.storage.extra.DarkMageSpell;
import me.hapyl.fight.game.heroes.storage.extra.WitherData;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HealingAura extends DarkMageTalent {

    @DisplayField(suffix = "blocks") private final double radius = 2.5d;
    @DisplayField private final double assistHealing = 10.0d;

    public HealingAura() {
        super("Healing Aura", "Creates a healing circle at your location that heals all players periodically.", Material.APPLE);

        setAssistDescription("Instantly heals for &c%s ❤&7.".formatted(assistHealing));

        setDuration(200);
        setCdSec(30);
    }

    @Override
    public void assist(WitherData data) {
        GamePlayer.getPlayer(data.player).heal(assistHealing);
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
        final Location location = player.getLocation();

        new GameTask() {
            private int tick = getDuration();
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

                if (tick-- <= 0) {
                    cancel();
                }
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
