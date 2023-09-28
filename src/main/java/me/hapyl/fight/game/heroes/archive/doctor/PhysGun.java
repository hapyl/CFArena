package me.hapyl.fight.game.heroes.archive.doctor;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.RightClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.Map;

public class PhysGun extends Weapon implements RightClickable {

    private final Map<Player, LivingEntity> capturedEntity = Maps.newHashMap();
    private final Map<Player, Boolean> flightMap = Maps.newHashMap();

    public PhysGun() {
        super(Material.GOLDEN_HORSE_ARMOR);
        setId("dr_ed_gun_2");
        setName("Upgraded Dr. Ed's Gravity Energy Capacitor Mk. 4");

        setAbility(AbilityType.RIGHT_CLICK, Ability.of("Harvest V2", "", this));
        GameTask.scheduleCancelTask(capturedEntity::clear);
    }

    @Override
    public void onRightClick(@Nonnull Player player, @Nonnull ItemStack item) {
        // Throw
        if (capturedEntity.containsKey(player)) {
            final LivingEntity entity = capturedEntity.get(player);
            final Location location = player.getLocation().add(player.getLocation().getDirection().multiply(2.0d));

            capturedEntity.remove(player);

            if (entity instanceof Player targetPlayer) {
                targetPlayer.setAllowFlight(flightMap.getOrDefault(targetPlayer, false));
                flightMap.remove(targetPlayer);
            }

            entity.setVelocity(player.getLocation().getDirection().multiply(2.0d));
            PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 10, 0.2, 0.05, 0.2, 0.02f);
            PlayerLib.playSound(location, Sound.ITEM_CROSSBOW_SHOOT, 0.5f);
            return;
        }

        // Get the target entity
        final LivingGameEntity target = Collect.targetEntity(player, 3.0d, e -> e.isNot(player));

        if (target == null) {
            Chat.sendMessage(player, "&cNo valid target!");
            return;
        }

        capturedEntity.put(player, target.getEntity());
        target.asPlayer(targetPlayer -> {
            flightMap.put(targetPlayer, targetPlayer.getAllowFlight());
            targetPlayer.setAllowFlight(true);
        });

        // Tick entity
        new GameTask() {
            @Override
            public void run() {
                if (player.getInventory().getHeldItemSlot() != 4
                        || (capturedEntity.get(player) == null)
                        || (target.isNot(capturedEntity.get(player)))) {
                    dismountEntity(player, target.getEntity());
                    this.cancel();
                    return;
                }

                final Location playerLocation = player.getLocation();
                final Location location = target.getLocation();
                Location finalLocation = playerLocation.add(0.0d, 1.0d, 0.0d).add(playerLocation.getDirection().multiply(2.0d));

                finalLocation.setYaw(location.getYaw());
                finalLocation.setPitch(location.getPitch());

                if (!finalLocation.getBlock().getType().isAir() || !finalLocation.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
                    finalLocation = playerLocation;
                }

                target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1, true));
                target.sendActionbar("&f&lCaptured by &a%s&f&l!", player.getName());

                target.teleport(finalLocation);
                Chat.sendTitle(player, "", "&f&lCarrying &a%s".formatted(target.getName()), 0, 10, 0);

            }
        }.runTaskTimer(0, 1);
    }

    private void dismountEntity(Player player, LivingEntity entity) {
        final Location location = entity.getLocation();
        final Block block = location.getBlock();

        if (!block.getType().isAir() || !block.getRelative(BlockFace.UP).getType().isAir()) {
            Chat.sendMessage(player, "&a%s was teleported to your since they would suffocate.", entity.getName());
            entity.teleport(player);
        }

        boolean solid = false;
        // check for solid ground
        for (double y = 0; y <= location.getY(); ++y) {
            if (!location.clone().subtract(0.0d, y, 0.0d).getBlock().getType().isAir()) {
                solid = true;
                break;
            }
        }

        if (!solid) {
            Chat.sendMessage(player, "&a%s was teleported to your since they would fall into void.", entity.getName());
            entity.teleport(player);
        }

        player.setAllowFlight(flightMap.getOrDefault(player, false));
        capturedEntity.remove(player);
        flightMap.remove(player);
    }

}
