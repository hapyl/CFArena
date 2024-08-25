package me.hapyl.fight.game.talents.bloodfiend;


import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class TwinClaws extends Talent implements Listener {

    @DisplayField protected final double twinClawDamage = 10.0d;
    @DisplayField(scaleFactor = 100, suffix = "%", suffixSpace = false) protected final double bittenDamageIncrease = 1.5d;

    public TwinClaws(@Nonnull Key key) {
        super(key, "Twin Claws");

        setDescription("""
                Launch two &6giant claws&7. One in front, one behind.
                
                If a &6claw&7 hits an &cenemy&7, it deals &c{twinClawDamage} ❤ &cdamage&7 to them.
                &8;;Bitten enemies suffer more damage.
                """
        );

        setItem(Material.ACACIA_FENCE);
        setDuration(30);
        setCooldownSec(15);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);

        final Vector vectorFront = location.getDirection().normalize().multiply(0.5d);
        final Location locationFront = location.clone().add(vectorFront);

        final Vector vectorBack = vectorFront.clone().multiply(-1);
        final Location locationBack = location.clone().add(vectorBack);

        final int duration = getDuration();

        new TwinClaw(player, locationFront, vectorFront, duration);
        new TwinClaw(player, locationBack, vectorBack, duration);

        // Fx
        player.swingMainHand();

        return Response.OK;
    }
}
