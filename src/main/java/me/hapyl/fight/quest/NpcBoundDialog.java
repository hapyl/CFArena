package me.hapyl.fight.quest;

import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

// TODO (Tue, Feb 11 2025 @xanyjl): Maybe add helper method to remove the need for passing the npc reference all the time
public class NpcBoundDialog extends CFDialog {

    private static final double DISTANCE_THRESHOLD = 20.0d;

    private final HumanNPC npc;
    private int tick;

    public NpcBoundDialog(@Nonnull HumanNPC npc) {
        this.npc = npc;
    }

    public void entry(@Nonnull String... strings) {
        addEntry(npc, strings);
    }

    @Override
    public void onDialogTick(@Nonnull Player player) {
        if (tick++ % 5 != 0) {
            return;
        }

        // Don't allow going to far away from the npc
        final Location npcLocation = npc.getLocation();
        final Location location = player.getLocation();

        final double distance = npcLocation.distanceSquared(location);

        if (distance < DISTANCE_THRESHOLD) {
            return;
        }

        final Vector vector = npcLocation.toVector().subtract(location.toVector()).normalize().multiply(0.5d);
        player.setVelocity(vector);
    }
}
