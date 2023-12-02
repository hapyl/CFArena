package me.hapyl.fight.game.talents.archive.ninja;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class NinjaDash extends Talent {

    @DisplayField private final float magnitude = 1.5f;

    public NinjaDash() {
        super("Dashing Wind", "Instantly propel yourself into the direction you're looking.");

        setItem(Material.FEATHER);
        setCooldown(100);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Vector vector = player.getLocation().getDirection();

        player.setVelocity(new Vector(vector.getX(), 0, vector.getZ()).normalize().multiply(magnitude));
        player.playWorldSound(Sound.ITEM_TRIDENT_RIPTIDE_1, 1.1f);

        return Response.OK;
    }
}
