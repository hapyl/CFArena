package me.hapyl.fight.game.talents.moonwalker;

import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MoonSliteBomb extends Talent implements Listener {

    private final Map<UUID, Set<Item>> bombs = new HashMap<>();

    @DisplayField private final short bombLimit = 3;
    @DisplayField private final double explosionRadius = 2.5d;
    @DisplayField private final double explosionDamage = 5.0d;
    @DisplayField private final int explosionDuration = 600;
    @DisplayField private final int corrosionDuration = 35;

    public MoonSliteBomb(@Nonnull Key key) {
        super(key, "Moonslite Bomb");

        setDescription("""
                Drop a proximity grenade at your current location that explodes on contact with enemy or after a set period, dealing damage and applying &6&lCorrosion &7for a short time.
                
                &6;;You can only have {bombLimit} bombs at the time.
                """
        );

        setMaterial(Material.END_STONE_BRICK_SLAB);
        setCooldownSec(10);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final UUID uniqueId = player.getUUID();

        Nulls.runIfNotNull(bombs.get(uniqueId), set -> {
            if (set.isEmpty()) {
                return;
            }
            set.forEach(Entity::remove);
        });

        bombs.remove(uniqueId);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        bombs.values().forEach(items -> items.forEach(Item::remove));
        bombs.clear();
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Set<Item> playerBombs = getBombs(player);
        if (playerBombs.size() >= bombLimit) {
            return Response.error("Limit reached!");
        }

        final Item item = player.getWorld().dropItem(player.getLocation(), new ItemStack(this.getItem(player).getType()));
        item.setPickupDelay(20);
        item.setTicksLived(6000 - explosionDuration);
        item.setOwner(player.getUUID());
        playerBombs.add(item);

        // Fx
        new GameTask() {
            @Override
            public void run() {
                if (item.isDead()) {
                    this.cancel();
                    return;
                }

                final Location location = item.getLocation().clone().add(0.0d, 0.15d, 0.0d);
                PlayerLib.spawnParticle(location, Particle.END_ROD, 1, 0.1d, 0.0d, 0.1d, 0.01f);
            }
        }.runTaskTimer(0, 5);

        return Response.OK;
    }

    public int getBombSize(GamePlayer player) {
        return getBombs(player).size();
    }

    private Set<Item> getBombs(GamePlayer player) {
        return getBombs(player.getUUID());
    }

    private Set<Item> getBombs(UUID uuid) {
        return bombs.computeIfAbsent(uuid, k -> new HashSet<>());
    }

    @EventHandler()
    public void handleEntityPickupItemEvent(EntityPickupItemEvent ev) {
        final Item item = ev.getItem();
        if (isBombItem(item)) {
            ev.setCancelled(true);
            if (!CFUtils.compare(item.getOwner(), ev.getEntity().getUniqueId())) {
                explode(item);
            }
        }
    }

    @EventHandler()
    public void handleItemDespawnEvent(ItemDespawnEvent ev) {
        final Item item = ev.getEntity();
        if (isBombItem(item)) {
            explode(item);
        }
    }

    private void explode(Item item) {
        final UUID owner = item.getOwner();
        if (owner != null) {
            getBombs(owner).remove(item);
        }

        CFUtils.createExplosion(item.getLocation(), explosionRadius, explosionDamage, this::applyCorrosion);
        item.remove();
    }

    private void applyCorrosion(LivingGameEntity entity) {
    }

    private boolean isBombItem(Item item) {
        for (final Set<Item> value : bombs.values()) {
            for (final Item item1 : value) {
                if (item1.equals(item)) {
                    return true;
                }
            }
        }
        return false;
    }
}
