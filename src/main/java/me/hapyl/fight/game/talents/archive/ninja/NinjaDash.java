package me.hapyl.fight.game.talents.archive.ninja;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NinjaDash extends Talent {

    @DisplayField private final float magnitude = 1.5f;

    public NinjaDash() {
        super("Dashing Wind", "Instantly propel yourself into direction you looking.");

        setItem(Material.FEATHER);
        setCooldown(100);
    }

    @Override
    public Response execute(Player player) {
        final Vector vector = player.getLocation().getDirection();

        player.setVelocity(new Vector(vector.getX(), 0, vector.getZ()).normalize().multiply(magnitude));
        PlayerLib.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1.1f);

        return Response.OK;
    }
}
