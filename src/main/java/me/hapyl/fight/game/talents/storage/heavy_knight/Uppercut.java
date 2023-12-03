package me.hapyl.fight.game.talents.storage.heavy_knight;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Uppercut extends Talent {

    @DisplayField private final double range = 5.0d;
    @DisplayField private final double height = 3.0d;

    public Uppercut() {
        super("Uppercut", "Knock you enemies up in the air.");

        setItem(Material.IRON_BLOCK);
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getLocation();
        final Vector vector = location.getDirection().normalize().setY(0.0d);

        location.add(vector.multiply(3.0d));

        Utils.getEntitiesInRange(location, range).forEach(entity -> {
            if (!Utils.isEntityValid(entity, player)) {
                return;
            }

            entity.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20, 0));
            entity.setVelocity(BukkitUtils.vector3Y(height));
        });

        return Response.OK;
    }
}
