package me.hapyl.fight.game.talents.storage.orc;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class OrcGrowl extends Talent {

    @DisplayField private final int debuffDuration = Tick.fromSecond(6);
    @DisplayField private final double distance = 8.0;

    public OrcGrowl() {
        super("Beast's Grown");

        setItem(Material.GOAT_HORN);
        setDurationSec(5);
        setCdSec(20);
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getLocation();

        player.addPotionEffect(PotionEffectType.SLOW.createEffect(20, 3));

        Geometry.drawCircleAnchored(location, distance, Quality.HIGH, new WorldParticle(Particle.CRIT_MAGIC), 1.0d);

        Utils.getEntitiesInRangeValidateRange(location, distance).forEach(victim -> {
            if (victim == player) {
                return;
            }

            victim.addPotionEffect(PotionEffectType.SLOW.createEffect(debuffDuration, 4));
            victim.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(debuffDuration, 0));
        });

        return Response.OK;
    }
}
