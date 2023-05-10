package me.hapyl.fight.game.collectible.relic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.Unique;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.util.Range;
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
import java.util.function.Consumer;

public class RelicHunt extends DependencyInjector<Main> implements Listener {

    private final Map<Integer, Relic> byId;
    private final Map<Type, List<Relic>> byType;
    private final Map<GameMaps, List<Relic>> byZone;

    private final Map<Integer, Reward> collectorRewards;
    private final Map<Integer, Reward> exchangeReward;

    public RelicHunt(Main plugin) {
        super(plugin);
        byId = Maps.newHashMap();
        byType = Maps.newHashMap();
        byZone = Maps.newHashMap();

        collectorRewards = Maps.newHashMap();
        exchangeReward = Maps.newHashMap();

        collectorRewards.put(1, new RelicCollectorReward(1).withCoins(500).withExp(5));
        collectorRewards.put(2, new RelicCollectorReward(2).withCoins(1000).withExp(10));
        collectorRewards.put(3, new RelicCollectorReward(3).withCoins(2000).withExp(20).withRubies(1));

        exchangeReward.put(1, new ExchangeReward(1).withCoins(500).withExp(5));
        exchangeReward.put(2, new ExchangeReward(2).withCoins(1000).withExp(10));
        exchangeReward.put(3, new ExchangeReward(3).withCoins(1500).withExp(15));
        exchangeReward.put(4, new ExchangeReward(4).withCoins(2000).withExp(20));
        exchangeReward.put(5, new ExchangeReward(5).withCoins(3000).withExp(30).withRubies(1));

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, new RelicRunnable(this), 0L, 20L);

        // Register relics
        registerRelics();

        // Compute types and zones
        byId.forEach((id, relic) -> {
            computeMapList(byZone, relic.getZone(), list -> list.add(relic));
            computeMapList(byType, relic.getType(), list -> list.add(relic));
        });

        createRelics();
    }

    @Nullable
    public Reward getCollectorReward(@Range(min = 1) int tier) {
        return collectorRewards.get(tier);
    }

    @Nonnull
    public Reward getExchangeReward(@Range(min = 1) int tier) {
        Reward reward = exchangeReward.get(tier);

        if (reward == null) { // default to the last reward
            reward = exchangeReward.get(exchangeReward.size() - 1);
        }

        if (reward == null) {
            throw new IllegalStateException("There must be at least one exchange reward!");
        }

        return reward;
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

    public int countIn(GameMaps map) {
        return byZone.getOrDefault(map, Lists.newArrayList()).size();
    }

    public boolean anyIn(GameMaps map) {
        return countIn(map) > 0;
    }

    /**
     * Gets relics in the given zone.
     * Prefer {@link #countIn(GameMaps)} if you just need the count.
     *
     * @param map - Zone.
     * @return list of relics in the given zone.
     */
    @Nonnull
    public List<Relic> byZone(GameMaps map) {
        return Lists.newArrayList(byZone.getOrDefault(map, Lists.newArrayList()));
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

    public List<Relic> getFoundList(Player player) {
        return byId.values().stream().filter(relic -> relic.hasFound(player)).toList();
    }

    public List<Relic> getFoundListIn(Player player, GameMaps zone) {
        return getFoundList(player).stream().filter(relic -> relic.getZone() == zone).toList();
    }

    public List<Relic> getFoundListByType(Player player, Type value) {
        return getFoundList(player).stream().filter(relic -> relic.getType() == value).toList();
    }

    public void forEach(@Nonnull BiConsumer<Integer, Relic> consumer) {
        byId.forEach(consumer);
    }

    @Nonnull
    public List<Relic> byType(Type value) {
        return Lists.newArrayList(byType.getOrDefault(value, Lists.newArrayList()));
    }

    private void registerRelics() {
        // Lobby
        registerRelic(100, new Relic(Type.AMETHYST, 27, 66, 8));
        registerRelic(101, new Relic(Type.AMETHYST, 32, 66, 0));
        registerRelic(102, new Relic(Type.EMERALD, -20, 72, 21).setBlockFace(BlockFace.SOUTH_WEST));
        registerRelic(103, new Relic(Type.AMETHYST, 11, 67, -27));
        registerRelic(104, new Relic(Type.AMETHYST, 7, 66, 23));

        // Arena
        registerRelic(200, new Relic(Type.SAPPHIRE, 70, 70, 18).setZone(GameMaps.ARENA));
        registerRelic(201, new Relic(Type.EMERALD, 66, 78, -5).setZone(GameMaps.ARENA));
        registerRelic(202, new Relic(Type.EMERALD, 62, 80, 14).setZone(GameMaps.ARENA));
        registerRelic(203, new Relic(Type.EMERALD, 112, 68, -30).setZone(GameMaps.ARENA));

        // Japan - Reversed Ids in 300-399 range
        // Skipping for now, since rebuilding -h

        // Greenhouse
        registerRelic(400, new Relic(Type.EMERALD, -98, 62, -21).setZone(GameMaps.GREENHOUSE));
        registerRelic(401, new Relic(Type.EMERALD, -99, 71, -1).setZone(GameMaps.GREENHOUSE));

        // Railway (Old)
        registerRelic(500, new Relic(Type.SAPPHIRE, 38, 63, 101).setZone(GameMaps.RAILWAY));
        registerRelic(501, new Relic(Type.SAPPHIRE, 36, 65, 77).setZone(GameMaps.RAILWAY));
        registerRelic(502, new Relic(Type.EMERALD, -9, 66, 117).setZone(GameMaps.RAILWAY));

        // Winery
        registerRelic(600, new Relic(Type.SAPPHIRE, 231, 62, 216).setZone(GameMaps.WINERY));
        registerRelic(601, new Relic(Type.DIAMOND, 223, 62, 190).setZone(GameMaps.WINERY).setBlockFace(13));
    }

    private <K, V> void computeMapList(final Map<K, List<V>> map, K key, final Consumer<List<V>> consumer) {
        map.compute(key, (ref, list) -> {
            if (list == null) {
                list = Lists.newArrayList();
            }

            consumer.accept(list);
            return list;
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
                    playerProfile.getTextures()
                            .setSkin(new URL("http://textures.minecraft.net/texture/%s".formatted(relic.getType().getTexture())));

                    skull.setOwnerProfile(playerProfile);
                    skull.update(true, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void registerRelic(@Unique final int id, final Relic relic) throws IllegalArgumentException {
        if (byId.containsKey(id)) {
            throw new IllegalArgumentException("Id %s is already taken by %s!".formatted(id, byId.get(id)));
        }

        if (relic.getId() != -1) {
            throw new IllegalArgumentException("%s is already registered with Id %s".formatted(relic, relic.getId()));
        }

        relic.setId(id);
        byId.put(id, relic);
    }
}
