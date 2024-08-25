package me.hapyl.fight.game.talents.dark_mage;


import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.dark_mage.SpellButton;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class HealingAura extends DarkMageTalent {

    @DisplayField(suffix = "blocks") private final double radius = 2.5d;
    @DisplayField private final double healing = 2.0d;
    @DisplayField private final double instantHealing = 10.0d;
    @DisplayField private final int healingPeriod = 15;

    public HealingAura(@Nonnull Key key) {
        super(key, "Healing Aura", """
                Instantly heal for &c{instantHealing} ❤&7 and create a &ahealing&7 aura at your &ncurrent&7 &nlocation&7 that &aheals&7 &nall&7 nearby players.
                """);

        setType(TalentType.SUPPORT);
        setItem(Material.APPLE);
        setDurationSec(6);
        setCooldownSec(30);
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
        final Location location = player.getLocation().add(0, 1, 0);

        player.heal(instantHealing);

        new TimedGameTask(this) {
            private double theta = 0;

            @Override
            public void run(int tick) {
                // Heal
                if (modulo(healingPeriod)) {
                    Collect.nearbyPlayers(location, radius).forEach(target -> {
                        target.heal(healing);
                        target.playWorldSound(Sound.BLOCK_GRASS_HIT, 1.0f);
                    });
                }

                // Fx
                final double x = Math.sin(theta) * radius;
                final double y = Math.sin(Math.toRadians(tick) * 16) * 0.4;
                final double z = Math.cos(theta) * radius;

                LocationHelper.modify(location, x, y, z, then -> {
                    player.spawnWorldParticle(then, Particle.HAPPY_VILLAGER, 2, 0.01, 0, 0.01, 0);
                });

                theta += Math.PI / 26;
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}
