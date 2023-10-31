package me.hapyl.fight.game.talents.archive.shaman;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.ConcurrentPlayerMap;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Set;

public class Totem extends Talent {

    private final int MAX_TOTEMS = 3;
    private final int PLACE_CD = 120;
    private final int DESTROY_CD = 60;
    private final ConcurrentPlayerMap<LinkedList<ActiveTotem>> playerTotems = new ConcurrentPlayerMap<>();

    public Totem() {
        super(
                "Totem",
                """
                        Place a totem on your target location. Target it and use other abilities to activate it.
                                                
                        Using this ability while targeting placed totem will destroy it.
                        """,
                Material.OBSIDIAN
        );

        addAttributeDescription("Cooldown", BukkitUtils.roundTick(PLACE_CD) + "s");
        addAttributeDescription("Destroy Cooldown: &l%ss", BukkitUtils.roundTick(DESTROY_CD) + "s");
        addAttributeDescription("Maximum Totems &l%s", MAX_TOTEMS);
    }

    @Override
    public void onStart() {
        final int tickPeriod = 5;

        new GameTask() {
            private int tick = 0;

            @Override
            public void run() {
                playerTotems.values().forEach(totems -> totems.forEach(totem -> {
                    final ResonanceType resonanceType = totem.getResonanceType();
                    final int interval = resonanceType.getInterval();
                    if (interval != 0 && tick % interval != 0) {
                        return;
                    }

                    resonanceType.resonate(totem);
                }));
                tick += tickPeriod;
            }
        }.runTaskTimer(0, tickPeriod);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        getPlayerTotems(player).forEach(ActiveTotem::destroy);
        playerTotems.remove(player);
    }

    @Override
    public void onStop() {
        playerTotems.values().forEach(totems -> totems.forEach(ActiveTotem::destroy));
        playerTotems.clear();
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        // Check for destroying
        final ActiveTotem targetTotem = getTargetTotem(player);

        if (targetTotem != null) {
            getPlayerTotems(player).remove(targetTotem);
            targetTotem.destroy();
            startCd(player, DESTROY_CD);
            return Response.OK;
        }

        final Block block = player.getTargetBlockExact(10);

        if (block == null) {
            return Response.error("Cannot place there.");
        }

        final Block origin = block.getRelative(BlockFace.UP);
        final Block plate = origin.getRelative(BlockFace.UP);

        final Location location = origin.getLocation();

        if (!origin.getType().isAir() || !plate.getType().isAir()) {
            return Response.error("Cannot fit totem!");
        }

        final LinkedList<ActiveTotem> totems = getPlayerTotems(player);
        if (totems.size() >= MAX_TOTEMS) {
            final ActiveTotem first = totems.pollFirst();
            if (first != null) {
                first.destroy();
            }
        }

        final ActiveTotem totem = new ActiveTotem(player, location);
        totems.add(totem);
        totem.create();

        startCd(player, PLACE_CD);
        return Response.OK;
    }

    @Nullable
    public ActiveTotem getTargetTotem(GamePlayer player) {
        // TODO: 019, Mar 19, 2023 - Maybe use dot
        final Location location = player.getLocation().add(0, 1.5, 0);
        final Vector vector = location.getDirection().normalize();

        for (double i = 0; i < 100; i += 0.5) {
            double x = vector.getX() * i;
            double y = vector.getY() * i;
            double z = vector.getZ() * i;
            location.add(x, y, z);

            for (final Shulker shulker : getShulkersNearby(location)) {
                final LinkedList<ActiveTotem> totems = getPlayerTotems(player);
                if (totems.isEmpty()) {
                    continue;
                }

                for (ActiveTotem totem : totems) {
                    if (totem.isShulker(shulker) && totem.getPlayer().equals(player)) {
                        return totem;
                    }
                }
            }

            location.subtract(x, y, z);
        }

        return null;
    }

    @Nonnull
    public LinkedList<ActiveTotem> getPlayerTotems(GamePlayer player) {
        return playerTotems.computeIfAbsent(player, m -> Lists.newLinkedList());
    }

    private Set<Shulker> getShulkersNearby(Location location) {
        final World world = location.getWorld();
        final Set<Shulker> set = Sets.newHashSet();

        if (world == null) {
            return set;
        }

        for (Entity entity : world.getNearbyEntities(location, 1, 1, 1)) {
            if (entity instanceof Shulker shulker) {
                set.add(shulker);
            }
        }

        return set;
    }

    public void defaultAllTotems(GamePlayer player) {
        for (ActiveTotem totem : getPlayerTotems(player)) {
            totem.defaultColor();
        }
    }
}
