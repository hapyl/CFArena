package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.dark_mage.SpellButton;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class HealingAura extends DarkMageTalent {

    @DisplayField(suffix = "blocks") private final double radius = 2.5d;
    @DisplayField private final double healing = 2.0d;
    @DisplayField private final double assistHealing = 25.0d;
    @DisplayField private final int healingPeriod = 15;

    public HealingAura() {
        super("Healing Aura", """
                Create a &ahealing&7 circle at your location that periodically &aheals&7 &nall&7 nearby players.
                """);

        setType(Type.SUPPORT);
        setItem(Material.APPLE);
        setDurationSec(10);
        setCooldownSec(30);
    }

    @Nonnull
    @Override
    public String getAssistDescription() {
        return "Instantly &aheal&7 for &c%.0f ❤&7.".formatted(assistHealing);
    }

    @Override
    public void assist(@Nonnull WitherData data) {
        data.player.heal(assistHealing);
    }

    @Nonnull
    @Override
    public SpellButton first() {
        return SpellButton.LEFT;
    }

    @Nonnull
    @Override
    public SpellButton second() {
        return SpellButton.LEFT;
    }

    @Override
    public Response executeSpell(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        new TimedGameTask(this) {
            private double theta = 0;

            @Override
            public void run(int tick) {
                // Heal
                if (modulo(healingPeriod)) {
                    Collect.nearbyPlayers(location, radius).forEach(target -> {
                        target.heal(healing);
                        target.playSound(Sound.BLOCK_GRASS_HIT, 1.0f);
                    });
                }

                // Fx
                final double x = radius * Math.sin(theta);
                final double z = radius * Math.cos(theta);

                location.add(x, 0, z);
                player.spawnWorldParticle(location, Particle.VILLAGER_HAPPY, 2, 0.01, 0, 0.01, 0);
                location.subtract(x, 0, z);

                theta = theta >= Math.PI * 2 ? 0 : theta + Math.PI / 32;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
