package me.hapyl.fight.game.talents.archive.troll;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Repulsor extends Talent {

    @DisplayField(suffix = "blocks") private final double radius = 10.0d;

    public Repulsor() {
        super(
                "Repulsor",
                "Propels all nearby opponents high up into the sky!"
        );

        setType(Type.IMPAIR);
        setItem(Material.IRON_BOOTS);
        setCooldown(200);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        Collect.nearbyEntities(player.getLocation(), radius).forEach(victim -> {
            if (player.isSelfOrTeammate(victim)) {
                return;
            }

            victim.sendMessage("&aWhoosh!");
            victim.setVelocity(new Vector(0.0d, 1.0d, 0.0d));
        });

        player.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 1.8f);

        return Response.OK;
    }
}
