package me.hapyl.fight.fx;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.annotate.ForceCloned;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class Riptide implements Removable {

    private static final double Y_OFFSET = 2.0d;

    private final HumanNPC npc;

    public Riptide(@Nonnull @ForceCloned Location riptideLocation) {
        final Location location = BukkitUtils.newLocation(riptideLocation);
        location.add(0.0d, Y_OFFSET, 0.0d);
        location.setPitch(90f);

        npc = new HumanNPC(location, null, "");
        npc.bukkitEntity().setInvisible(true);
        npc.setCollision(false);
        npc.showAll();

        npc.setDataWatcherByteValue(8, (byte) 0x04);
        npc.updateDataWatcher();
    }

    public void teleport(@Nonnull Location location) {
        location.add(0.0d, Y_OFFSET, 0.0d);
        location.setPitch(90f);

        npc.teleport(location);
    }

    @Override
    public void remove() {
        npc.remove();
    }
}
