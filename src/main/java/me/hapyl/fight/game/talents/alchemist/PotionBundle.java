package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class PotionBundle extends Talent {

    @DisplayField private final double magnitude = 0.9d;
    @DisplayField private final double maxY = 0.5d;

    public PotionBundle(@Nonnull Key key) {
        super(key, "Bundle o' Potions");

        setDescription("""
                Dash forward and throw a deadly potion behind.
                """);

        setItem(Material.BUNDLE);
        setType(TalentType.MOVEMENT);

        setCooldownSec(9);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final Vector direction = location.getDirection();

        direction.multiply(magnitude);
        direction.setY(maxY);

        player.setVelocity(direction);

        // Fx

        return Response.OK;
    }
}
