package me.hapyl.fight.game.maps.features;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.BlockLocation;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Booster {

    protected static final Map<BlockLocation, Booster> byLocation = new HashMap<>();

    private final EnumLevel designatedMap;
    private final BlockLocation location;
    private final Vector vector;

    public Booster(BlockLocation loc, Vector vec, boolean debug) {
        this(EnumLevel.CLOUDS, loc, vec, debug);
    }

    public Booster(EnumLevel map, BlockLocation loc, Vector vec, boolean debug) {
        this.designatedMap = map;
        this.location = loc;
        this.vector = vec;
        if (!debug) {
            byLocation.put(loc, this);
        }
    }

    public Booster(EnumLevel map, BlockLocation loc, Vector vec) {
        this(map, loc, vec, false);
    }

    public Booster(int x, int y, int z, double vecX, double vecY, double vecZ) {
        this(new BlockLocation(x, y, z), new Vector(vecX, vecY, vecZ), false);
    }

    public Booster(EnumLevel map, int x, int y, int z, double vecX, double vecY, double vecZ) {
        this(map, new BlockLocation(x, y, z), new Vector(vecX, vecY, vecZ), false);
    }

    public EnumLevel getDesignatedMap() {
        return designatedMap;
    }

    public Entity launch(boolean debug) {
        final Location location = this.location.centralize();
        final ArmorStand booster = Entities.ARMOR_STAND.spawn(location.add(0.0d, 1.25d, 0.0d), me -> {
            me.setSilent(true);
            me.setInvulnerable(true);
            me.setSmall(true);
            if (debug) {
                me.setCustomName(this.vector.toString());
                me.setCustomNameVisible(true);
            }
            else {
                me.setVisible(false);
            }
        });

        GameTask.runLater(() -> booster.setVelocity(this.vector), 5);
        return booster;
    }

    public Entity launchAndRide(GamePlayer player, boolean flag) {
        final Entity piggy = launch(flag);
        piggy.addPassenger(player.getPlayer());

        player.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 200);
        player.playWorldSound(Sound.ENTITY_GENERIC_EXPLODE, 2.0f);
        return piggy;
    }

    public BlockLocation getLocation() {
        return location;
    }

    @Nullable
    public static Booster byLocation(BlockLocation location) {
        for (final BlockLocation other : byLocation.keySet()) {
            if (other.compare(location)) {
                return byLocation.get(other);
            }
        }
        return null;
    }


}
