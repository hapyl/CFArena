package me.hapyl.fight.game.heroes.archive.ender;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.RightClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Blocks;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnderWeapon extends Weapon implements RightClickable {

    private final Map<Player, Location> targetLocation = new HashMap<>();
    private final int portKeyCooldown = 160;

    public EnderWeapon() {
        super(Material.ENDERMAN_SPAWN_EGG);

        setName("Fist");
        setId("ender_weapon");
        setDescription("Just a normal-sized fist.");
        setDamage(7.0);

        setAbility(AbilityType.RIGHT_CLICK, Ability.of("Transition", """
                Initiate transition to a target block, after a short casting time, teleport to the location.
                                
                Right Click again to cancel.
                """, this).setCooldown(portKeyCooldown)
        );
    }

    private boolean hasLocation(Player player) {
        return targetLocation.containsKey(player);
    }

    @Override
    public void onRightClick(@Nonnull Player player, @Nonnull ItemStack item) {
        if (player.hasCooldown(item.getType())) {
            return;
        }

        // Cancel
        if (hasLocation(player)) {
            targetLocation.remove(player);
            Chat.sendTitle(player, "", "&c&lCANCELLED", 0, 10, 10);
            PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 0.0f);
            return;
        }

        // Check for los block
        final Block targetBlock = player.getTargetBlockExact(25);

        if (!Blocks.isValid(targetBlock)) {
            Chat.sendMessage(player, "&cNo valid block in sight!");
            return;
        }

        // Initiate
        final List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 25);

        final Block preLastBlock = lastTwoTargetBlocks.get(0);
        final Block lastBlock = lastTwoTargetBlocks.get(1);

        if (preLastBlock == null || lastBlock == null) {
            Chat.sendMessage(player, "&cNo valid block in sight!");
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        if (preLastBlock.getType().isAir()) {
            final Block downBlock = lastBlock.getRelative(BlockFace.DOWN);
            if (downBlock.getLocation().equals(preLastBlock.getLocation())) {
                Chat.sendMessage(player, "&cCannot teleport from below a block!");
                return;
            }
        }

        final Location location = lastBlock.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);

        if (!location.getBlock().getType().isAir() || !location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
            Chat.sendMessage(player, "&cTarget location is not safe!");
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        targetLocation.put(player, location);

        new GameTask() {
            private final int maxWindupTime = 15;
            private int windupTime = maxWindupTime;

            @Override
            public void run() {
                if (!hasLocation(player)) {
                    this.cancel();
                    return;
                }

                if (windupTime > 0) {
                    final StringBuilder builder = new StringBuilder();
                    final int percentOfEight = (windupTime * 8 / maxWindupTime);
                    for (int i = 0; i < 8; i++) {
                        builder.append(i >= percentOfEight ? "&8" : "&a");
                        builder.append("-");
                    }

                    // Display fx at target block
                    Geometry.drawCircle(location, 1.2d, Quality.NORMAL, new WorldParticle(Particle.SPELL_WITCH));

                    PlayerLib.playSound(
                            player,
                            Sound.BLOCK_LEVER_CLICK,
                            Numbers.clamp((float) (1.3d - (0.1f * percentOfEight)), 0.0f, 2.0f)
                    );
                    Chat.sendTitle(player, "", builder.toString(), 0, 10, 0);
                    --windupTime;
                    return;
                }

                this.cancel();
                Chat.sendTitle(player, "", "&aTeleporting...", 0, 20, 10);
                // Teleport
                final Location playerLocation = player.getLocation();

                BukkitUtils.mergePitchYaw(playerLocation, location);
                Geometry.drawLine(playerLocation, location, 0.25f, new WorldParticle(Particle.PORTAL));

                player.teleport(location);
                GamePlayer.setCooldown(player, item.getType(), portKeyCooldown);
                targetLocation.remove(player);

                PlayerLib.playSound(playerLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);

            }
        }.runTaskTimer(0, 1);

    }

}
