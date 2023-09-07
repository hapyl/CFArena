package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class TwinClaws extends Talent implements Listener {

    @DisplayField protected final double twinClawDamage = 20.0d;
    @DisplayField(scaleFactor = 100, suffix = "%", suffixSpace = false) protected final double bittenDamageIncrease = 0.5d;

    public TwinClaws() {
        super("Twin Claws");

        setDescription("""
                Launch two giant claws. One in front, one behind.
                                
                If a claw hits an enemy, it deals &c{twinClawDamage} &c❤&7 to them; If the enemy is &cbitten&7, the damage is increased by &b%s%%.
                """, bittenDamageIncrease * 100);

        setItem(Material.ACACIA_FENCE);
        setDuration(30);
        setCooldownSec(15);
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);
        final World world = location.getWorld();

        if (world == null) {
            return Response.error("cannot spawn in an unloaded world");
        }

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
