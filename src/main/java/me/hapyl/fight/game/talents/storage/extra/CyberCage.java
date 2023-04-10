package me.hapyl.fight.game.talents.storage.extra;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.storage.techie.TrapCage;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

public class CyberCage {

    private final Player player;
    private LivingEntity entity;

    public CyberCage(Player player) {
        this.player = player;
        final Location location = player.getEyeLocation();

        final Item item = player.getWorld().dropItem(location.add(location.getDirection()), new ItemStack(Material.IRON_TRAPDOOR));
        item.setPickupDelay(99999);
        item.setTicksLived(5400); // 30s max lifetime
        item.setThrower(player.getUniqueId());
        item.setVelocity(player.getEyeLocation().getDirection().multiply(0.5d));

        new GameTask() {
            @Override
            public void run() {
                if (item.isOnGround() || item.isDead()) {
                    createWindupEntity(item.getLocation());
                    item.remove();
                    cancel();
                }
            }
        }.addCancelEvent(item::remove).runTaskTimer(0, 1);

    }

    public void activate(Player victim) {
        final LivingEntity marker = getEntity();
        final Player player = getPlayer();
        if (marker == null) {
            return;
        }

        if (player.isOnline()) {
            Chat.sendTitle(player, "&aCage Triggered!", "&7You caught %s".formatted(victim.getName()), 10, 20, 10);
        }

        final TrapCage talent = Talents.TRAP_CAGE.getTalent(TrapCage.class);

        GamePlayer.damageEntity(victim, talent.cageDamage, player);
        GamePlayer.getPlayer(victim).addEffect(GameEffectType.VULNERABLE, talent.vulnerabilityDuration);
        victim.setVelocity(marker.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize());

        // Glowing
        Glowing.glow(victim, ChatColor.AQUA, 40, player);

        // Zoom Fx
        PlayerLib.addEffect(victim, PotionEffectType.SLOW, 20, 300);
        PlayerLib.addEffect(victim, PotionEffectType.SLOW, 80, 1);
        PlayerLib.addEffect(victim, PotionEffectType.BLINDNESS, 30, 1);

        // Fx
        PlayerLib.playSound(victim, Sound.ENTITY_ENDERMAN_HURT, 0.8f);
    }

    private void createWindupEntity(Location location) {
        final Location fixedLocation = location.subtract(0.0d, 0.6d, 0.0d);

        final ArmorStand windupEntity = Entities.ARMOR_STAND.spawn(fixedLocation, self -> {
            self.setSmall(true);
            self.setMarker(true);
            self.setInvisible(true);

            Nulls.runIfNotNull(self.getEquipment(), eq -> eq.setHelmet(ItemBuilder.of(Material.IRON_TRAPDOOR).asIcon()));
        });

        GameTask.runLater(() -> {
            windupEntity.remove();
            createEntity(fixedLocation.subtract(0.0d, 0.35d, 0.0d));
        }, Talents.TRAP_CAGE.getTalent(TrapCage.class).windupTime);

        // Fx
        PlayerLib.playSound(player, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 0.75f);
    }

    private void createEntity(Location location) {
        final ItemStack maskedItem = getMaskedItem(location);
        entity = Entities.ARMOR_STAND.spawn(location, me -> {
            me.setSmall(true);
            me.setMarker(true);
            me.setInvisible(true);
            Nulls.runIfNotNull(me.getEquipment(), eq -> eq.setHelmet(maskedItem));
        });

        // Fx
        PlayerLib.playSound(player, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.2f);
    }

    private ItemStack getMaskedItem(Location location) {
        final Material type = location.getBlock().getRelative(BlockFace.DOWN).getType();
        return new ItemStack(type.isAir() ? Material.IRON_TRAPDOOR : type);
    }

    public void remove() {
        if (entity == null) {
            return;
        }

        entity.remove();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isOwner(Player player) {
        return player != null && getPlayer() == player;
    }

    public void drawParticle() {
        if (entity == null) {
            return;
        }

        final Location location = entity.getLocation();
        final Material type = location.getBlock().getType();

        if (!type.isAir()) {
            ParticleBuilder.blockBreak(type).display(location.add(0.0d, 1.0d, 0.0d));
            return;
        }

        PlayerLib.spawnParticle(location.add(0.0d, 1.0d, 0.0d), Particle.SNOWBALL, 1, 0, 0, 0, 0.01f);
    }

    @Nullable
    public LivingEntity getEntity() {
        return entity;
    }

    public boolean compareDistance(Location location, double distance) {
        return entity != null && entity.getLocation().distance(location) <= distance;
    }
}
