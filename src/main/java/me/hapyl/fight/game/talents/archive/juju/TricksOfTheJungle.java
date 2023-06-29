package me.hapyl.fight.game.talents.archive.juju;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;

public class TricksOfTheJungle extends Talent implements Listener {

    private final Map<Player, Integer> playerArrows = Maps.newHashMap();
    private final Set<Arrow> elusiveArrows = Sets.newHashSet();

    @DisplayField private final short maxArrows = 6;
    @DisplayField private final int cdBetweenShots = 10;
    @DisplayField private final double ySpread = -1.5d;
    @DisplayField private final double horizontalSpread = 1.55d;
    @DisplayField private final double damage = 5.0d;

    public TricksOfTheJungle() {
        super("Tricks of the Jungle", """
                Equip {maxArrows} elusive arrows.
                                
                &c&lFIRE &7your bow to instantly fire arrows, which explode and bloom upon hit, dealing moderate damage.
                """, Material.OAK_SAPLING);

        setDurationSec(8);
        setCooldownSec(20);
    }

    @Override
    public void onStart() {
        // Draw particle for Elusive Burst
        elusiveArrows.forEach(arrow -> PlayerLib.spawnParticle(arrow.getLocation(), Particle.TOTEM, 3, 0, 0, 0, 0));
    }

    @Override
    public void onDeath(Player player) {
        playerArrows.remove(player);
    }

    @Override
    public void onStop() {
        elusiveArrows.clear();
        playerArrows.clear();
    }

    @EventHandler()
    public void handleProjectileHit(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow
                && arrow.getShooter() instanceof Player player
                && elusiveArrows.contains(arrow)) {
            bloomArrow(player, arrow.getLocation());
        }
    }

    @EventHandler()
    public void handleBowCharge(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Action action = ev.getAction();

        if (playerArrows.getOrDefault(player, 0) <= 0
                || ev.getHand() == EquipmentSlot.OFF_HAND
                || (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR)) {
            return;
        }

        final ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() != Material.BOW) {
            return;
        }

        GamePlayer.getPlayer(player).interrupt();

        final Arrow arrow = player.launchProjectile(Arrow.class);
        elusiveArrows.add(arrow);

        player.setCooldown(Material.BOW, cdBetweenShots);

        final int currentArrows = playerArrows.compute(player, (p, i) -> i == null ? 1 : i - 1);

        // End if all arrows are used
        if (currentArrows <= 0) {
            playerArrows.remove(player);
        }
    }

    @Override
    public Response execute(Player player) {
        playerArrows.put(player, (int) maxArrows);

        // Display the number of arrows left
        GameTask.runDuration(this, (task, i) -> {
            final int arrows = playerArrows.getOrDefault(player, 0);

            if (i <= 0) {
                playerArrows.remove(player);
                return;
            }

            if (arrows <= 0) {
                task.cancel();
                return;
            }

            Chat.sendTitle(player,
                    "",
                    "&a🏹".repeat(arrows) + "&7🏹".repeat(maxArrows - arrows) + " &8| &b" + Utils.formatTick(i) + "s",
                    0, 10, 0
            );
        }, 0, 5);

        return Response.OK;
    }

    private void bloomArrow(Player player, Location location) {
        location.add(0, 2, 0);

        spawnArrow(player, location, new Vector(-horizontalSpread, ySpread, 0));
        spawnArrow(player, location, new Vector(horizontalSpread, ySpread, 0));
        spawnArrow(player, location, new Vector(0, ySpread, horizontalSpread));
        spawnArrow(player, location, new Vector(0, ySpread, -horizontalSpread));
        spawnArrow(player, location, new Vector(horizontalSpread, ySpread, horizontalSpread));
        spawnArrow(player, location, new Vector(horizontalSpread, ySpread, -horizontalSpread));
        spawnArrow(player, location, new Vector(-horizontalSpread, ySpread, horizontalSpread));
        spawnArrow(player, location, new Vector(-horizontalSpread, ySpread, -horizontalSpread));
    }

    private void spawnArrow(Player player, Location location, Vector vector) {
        if (location.getWorld() == null || !location.getBlock().getType().isAir()) {
            return;
        }

        final Arrow arrow = location.getWorld().spawnArrow(location, vector, 1.5f, 0.25f);

        arrow.setDamage(damage);
        arrow.setShooter(player);
    }
}
