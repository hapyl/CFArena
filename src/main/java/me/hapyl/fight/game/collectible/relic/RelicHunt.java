package me.hapyl.fight.game.collectible.relic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.Debugger;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.spigotutils.module.nbt.NBT;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.profile.PlayerProfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class RelicHunt extends DependencyInjector<Main> implements Listener {

    private final Map<Integer, Relic> byId;

    public RelicHunt(Main plugin) {
        super(plugin);
        this.byId = Maps.newHashMap();

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, new RelicRunnable(this), 0L, 20L);

        initRelics();
    }

    @EventHandler()
    public void handlePlayerInteract(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Block block = ev.getClickedBlock();

        if (ev.getHand() == EquipmentSlot.OFF_HAND || block == null || block.getType() != Material.PLAYER_HEAD) {
            return;
        }

        final Relic relic = byBlock(block);
        if (relic == null) {
            return;
        }

        if (!relic.hasFound(player)) {
            relic.give(player);
        }

    }

    private void initRelics() {
        //
        //        Important Note!
        //
        // DO NOT CHANGE THE ORDER OF THE
        // REGISTERED RELICS IN A ZONE, IT
        // WILL BREAK THE SYSTEM!
        //
        // A SINGLE ZONE MAY HAVE UP TO 100 RELICS

        // Lobby
        registerRelic(new Relic(Type.AMETHYST, 27, 66, 8));
        registerRelic(new Relic(Type.AMETHYST, 32, 66, 0));
        registerRelic(new Relic(Type.EMERALD, -20, 72, 21).setBlockFace(BlockFace.SOUTH_WEST));
        registerRelic(new Relic(Type.AMETHYST, 11, 67, -27));
        registerRelic(new Relic(Type.AMETHYST, 7, 66, 23));

        // Arena
        registerRelic(new Relic(Type.SAPPHIRE, 70, 70, 18).setZone(GameMaps.ARENA));
        registerRelic(new Relic(Type.EMERALD, 66, 78, -5).setZone(GameMaps.ARENA));
        registerRelic(new Relic(Type.EMERALD, 62, 80, 14).setZone(GameMaps.ARENA));
        registerRelic(new Relic(Type.EMERALD, 112, 68, -30).setZone(GameMaps.ARENA));

        // Japan
        // Skipping for now, since rebuilding -h

        // Greenhouse
        registerRelic(new Relic(Type.EMERALD, -98, 62, -21).setZone(GameMaps.GREENHOUSE));
        registerRelic(new Relic(Type.EMERALD, -99, 71, -1).setZone(GameMaps.GREENHOUSE));

        // Railway (Old)
        registerRelic(new Relic(Type.SAPPHIRE, 38, 63, 101).setZone(GameMaps.RAILWAY));
        registerRelic(new Relic(Type.SAPPHIRE, 36, 65, 77).setZone(GameMaps.RAILWAY));
        registerRelic(new Relic(Type.EMERALD, -9, 66, 117).setZone(GameMaps.RAILWAY));

        createRelics();

        // temp
        byId.forEach((i, r) -> {
            Debugger.log("#%s = %s", i, r);
        });
    }

    private void createRelics() {
        for (Relic relic : byId.values()) {
            final Location location = relic.getLocation().toLocation();
            final Block block = location.getBlock();

            block.setType(Material.PLAYER_HEAD, false);

            if (block.getBlockData() instanceof Rotatable rotatable) {
                rotatable.setRotation(relic.getBlockFace());
                block.setBlockData(rotatable, false);
            }

            if (block.getState() instanceof Skull skull) {
                try {
                    NBT.setInt(skull, "RelicId", relic.getId());

                    final PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
                    playerProfile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/%s".formatted(relic.getType().getTexture())));

                    skull.setOwnerProfile(playerProfile);
                    skull.update(true, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Gets relics in the given zone.
     * Prefer {@link #countIn(GameMaps)} if you just need the count.
     *
     * @param map - Zone.
     * @return list of relics in the given zone.
     */
    @Nonnull
    public List<Relic> getIn(GameMaps map) {
        final List<Relic> relics = Lists.newArrayList();

        for (Relic value : byId.values()) {
            if (value.getZone() == map) {
                relics.add(value);
            }
        }

        return relics;
    }

    public int countIn(GameMaps map) {
        int count = 0;

        for (Relic value : byId.values()) {
            if (value.getZone() == map) {
                count++;
            }
        }

        return count;
    }

    @Nullable
    public Relic byId(int id) {
        return byId.get(id);
    }

    @Nullable
    public Relic byBlock(Block block) {
        if (!(block.getState() instanceof Skull skull)) {
            return null;
        }

        final int id = NBT.getInt(skull, "RelicId");
        return byId(id);
    }

    private void registerRelic(Relic relic) {
        if (relic.getId() != -1) {
            return; // already registered
        }

        final GameMaps zone = relic.getZone();
        final int id = (100 * (zone.ordinal() + 1)) + countIn(zone);

        relic.setId(id);
        byId.put(id, relic);
    }

    public void forEach(@Nonnull BiConsumer<Integer, Relic> consumer) {
        byId.forEach(consumer);
    }
}
