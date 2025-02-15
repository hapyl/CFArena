package me.hapyl.fight.game.cosmetic.gadget;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class SnowballGadgetCosmetic extends Gadget implements Listener {

    private final Map<Snowball, GameTeam> snowballMap = Maps.newHashMap();
    private final Map<GameTeam, List<BlockData>> mappedMaterials = Maps.newHashMap();

    private final double radius = 6;
    private final int duration = Tick.fromSecond(4);

    public SnowballGadgetCosmetic(@Nonnull Key key) {
        super(key, "Colored Snowball");

        setDescription("""
                Throw a snowball that colors blocks it lands on into the color of your team.
                """);

        setRarity(Rarity.RARE);
        setIcon(Material.SNOWBALL);

        setCooldownSec(1);

        // Map materials
        mappedMaterials.put(GameTeam.RED, blockDataOf(Material.RED_WOOL, Material.RED_CONCRETE_POWDER));
        mappedMaterials.put(GameTeam.GREEN, blockDataOf(Material.GREEN_WOOL, Material.GREEN_CONCRETE_POWDER));
        mappedMaterials.put(GameTeam.BLUE, blockDataOf(Material.BLUE_WOOL, Material.BLUE_CONCRETE_POWDER));
        mappedMaterials.put(GameTeam.YELLOW, blockDataOf(Material.YELLOW_WOOL, Material.YELLOW_CONCRETE_POWDER));
        mappedMaterials.put(GameTeam.GOLD, blockDataOf(Material.ORANGE_WOOL, Material.ORANGE_CONCRETE_POWDER));
        mappedMaterials.put(GameTeam.AQUA, blockDataOf(Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_CONCRETE_POWDER));
        mappedMaterials.put(GameTeam.PINK, blockDataOf(Material.PINK_WOOL, Material.PINK_CONCRETE_POWDER));
        mappedMaterials.put(GameTeam.WHITE, blockDataOf(Material.WHITE_WOOL, Material.WHITE_CONCRETE_POWDER));
        mappedMaterials.put(GameTeam.BLACK, blockDataOf(Material.BLACK_WOOL, Material.BLACK_CONCRETE_POWDER));
    }

    @EventHandler
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        final Projectile entity = ev.getEntity();

        if (!(entity instanceof Snowball snowball)) {
            return;
        }

        final GameTeam team = snowballMap.remove(snowball);

        if (team == null) {
            return;
        }

        final Location location = entity.getLocation();

        // Fx here because I don't care
        PlayerLib.playSound(location, Sound.BLOCK_HONEY_BLOCK_SLIDE, 0.0f);
        PlayerLib.playSound(location, Sound.BLOCK_WOOL_PLACE, 0.0f);

        final List<Block> affectedBlocks = Lists.newArrayList();

        location.subtract(radius / 2, radius / 2, radius / 2);

        for (int x = 0; x < radius; x++) {
            for (int y = 0; y < radius; y++) {
                for (int z = 0; z < radius; z++) {
                    // Skip corners
                    final int edgeCount = (x == 0 || x == radius - 1 ? 1 : 0) +
                            (y == 0 || y == radius - 1 ? 1 : 0) +
                            (z == 0 || z == radius - 1 ? 1 : 0);

                    if (edgeCount >= 2) {
                        continue;
                    }

                    LocationHelper.offset(
                            location, x, y, z, () -> {
                                final Block block = location.getBlock();

                                // Only convert full blocks
                                final BoundingBox boundingBox = block.getBoundingBox();
                                final double volume = boundingBox.getVolume();

                                if (volume == 1.0d) {
                                    affectedBlocks.add(block);
                                }
                            }
                    );
                }
            }
        }

        // Realistically, this should never happen, but the check is needed because we're getting the
        // first block later and java is annoying and will throw a scary exception
        if (affectedBlocks.isEmpty()) {
            return;
        }

        changeColor(affectedBlocks, team);
    }

    @Nonnull
    @Override
    public Response execute(@Nonnull Player player) {
        final Snowball snowball = player.launchProjectile(Snowball.class);
        final GameTeam team = GameTeam.getEntryTeam(Entry.of(player));

        if (team == null) {
            return Response.error("You're not in a team!");
        }

        snowballMap.put(snowball, team);
        return Response.ok();
    }

    private List<BlockData> blockDataOf(Material... materials) {
        final List<BlockData> blockData = Lists.newArrayList();

        for (Material material : materials) {
            blockData.add(material.createBlockData());
        }

        return blockData;
    }

    private void changeColor(List<Block> affectedBlocks, GameTeam team) {
        final List<BlockData> colors = mappedMaterials.get(team);

        Bukkit.getOnlinePlayers().forEach(player -> {
            affectedBlocks.forEach(block -> {
                // Pick random color
                final BlockData color = CollectionUtils.randomElementOrFirst(colors);

                player.sendBlockChange(block.getLocation(), color);
            });
        });

        // Restore
        GameTask.runLater(
                () -> {
                    final Location location = affectedBlocks.getFirst().getLocation();

                    affectedBlocks.forEach(block -> block.getState().update(true, false));
                    affectedBlocks.clear();

                    // Fx
                    PlayerLib.playSound(location, Sound.BLOCK_GRASS_STEP, 0.0f);
                }, duration
        ).setShutdownAction(ShutdownAction.IGNORE);
    }
}
