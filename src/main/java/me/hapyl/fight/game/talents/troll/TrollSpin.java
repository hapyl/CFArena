package me.hapyl.fight.game.talents.troll;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class TrollSpin extends Talent {

    @DisplayField(suffix = "blocks") private final double radius = 30.0d;

    public TrollSpin(@Nonnull DatabaseKey key) {
        super(key, "Spin");

        setDescription("""
                Rotates all nearby opponents head 180 degrees.
                """
        );

        setType(TalentType.IMPAIR);
        setItem(Material.NAUTILUS_SHELL);
        setCooldown(300);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        Collect.nearbyEntities(player.getLocation(), radius).forEach(victim -> {
            if (player.isSelfOrTeammateOrHasEffectResistance(victim)) {
                return;
            }

            final Location location = victim.getLocation();
            location.setYaw(location.getYaw() + 180);

            victim.teleport(location);
            victim.playSound(Sound.ENTITY_BLAZE_HURT, 2.0f);
        });

        player.playSound(Sound.ENTITY_BLAZE_HURT, 0.75f);
        player.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2.0f);

        return Response.OK;
    }
}
