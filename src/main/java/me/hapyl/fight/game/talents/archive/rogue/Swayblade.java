package me.hapyl.fight.game.talents.archive.rogue;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class Swayblade extends Talent {

    @DisplayField(suffix = "blocks") private final double radius = 2.5d;
    @DisplayField private final float maxYawShift = 45.0f;
    @DisplayField private final float maxPitchShift = 25.0f;

    public Swayblade() {
        super("Swayblade");

        setDescription("""
                Hit all &cenemies&7 in front of you with the &bhilt&7 of your blade, &eimpairing&7 their vision.
                """);

        setItem(Material.GOLD_NUGGET);
        setType(Type.IMPAIR);
        setCooldownSec(4);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocationInFrontFromEyes(1);

        Collect.nearbyEntities(location, radius).forEach(entity -> {
            if (player.isSelfOrTeammate(entity)) {
                return;
            }

            final Location entityLocation = entity.getLocation();
            entityLocation.setYaw(entityLocation.getYaw() + player.random.nextFloatBool(maxYawShift + 1));
            entityLocation.setPitch(entityLocation.getPitch() + player.random.nextFloatBool(maxPitchShift + 1));

            entity.teleport(entityLocation);
        });

        // Fx
        player.swingMainHand();

        player.playWorldSound(Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 0.75f);
        player.playWorldSound(Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.75f);

        player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1);

        return Response.OK;
    }

}
