package me.hapyl.fight.game.heroes.doctor;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

// represents an active element currently held by the doctor
public class ActiveElement {

    private final GamePlayer player;
    private final Entity entity;
    private final Material material;
    private final ElementType type;

    private final ItemStack stack;
    private GameTask task;

    public ActiveElement(GamePlayer player, Block block) {
        final Location location = player.getLocation();
        this.player = player;
        this.material = block.getType();
        this.type = ElementType.getElement(block.getType());

        if (this.material == Material.PLAYER_HEAD || this.material == Material.PLAYER_WALL_HEAD) {
            final String texture = this.getHeadTexture(block);
            if (texture == null) {
                this.stack = new ItemStack(this.material);
            }
            else {
                this.stack = ItemBuilder.playerHead(texture).toItemStack();
            }
        }
        else {
            this.stack = new ItemStack(this.material);
        }

        this.entity = spawnBlockEntity(location.add(location.getDirection().multiply(2)));
    }

    public int getCooldown() {
        return type.getElement().getCd();
    }

    public void remove() {
        this.entity.remove();
    }

    public void throwEntity() {
        if (this.entity instanceof ArmorStand) {
            final ParticleBuilder particles = ParticleBuilder.blockDust(this.material);
            final String name = this.material.name();
            final ArmorStand stand = (ArmorStand) ActiveElement.this.entity;
            final AnimationType animationType = name.contains("SLAB") ?
                    AnimationType.SLAB :
                    name.contains("STAIRS") ? AnimationType.STAIRS : AnimationType.FULL_BLOCK;

            final Element element = type.getElement();

            new GameTask() {
                private int distance = 0;

                @Override
                public void run() {
                    if (distance++ >= 60) {
                        entityPoof();
                        this.cancel();
                        return;
                    }

                    final Location location = entity.getLocation();
                    final Location fixedLocation = entity.getLocation().clone().add(0.0d, 1.5d, 0.0d);
                    final Vector vector = location.getDirection();

                    entity.teleport(location.add(vector.multiply(1)));

                    // fx
                    particles.display(fixedLocation, 3, 0.1d, 0.05d, 0.1d, 0.015f);
                    stand.setHeadPose(stand.getHeadPose().add(animationType.getX(), animationType.getY(), animationType.getZ()));

                    // block hit detection
                    if (fixedLocation.getBlock().getType().isOccluding()) {
                        entityPoof();
                        this.cancel();
                        return;
                    }

                    final List<LivingGameEntity> players = Collect.nearbyEntities(ActiveElement.this.entity.getLocation(), 1.0d);
                    if (players.isEmpty()) {
                        return;
                    }

                    entityPoof();
                    players.forEach(target -> {
                        target.damage(element.getDamage(), player, EnumDamageCause.GRAVITY_GUN);
                        element.onHit(target.getEntity(), material);
                    });
                    this.cancel();

                }
            }.addCancelEvent(new BukkitRunnable() {
                @Override
                public void run() {
                    entity.remove();
                }
            }).runTaskTimer(0, 1);
        }
    }

    public void startTask() {
        stopTask();
        this.task = new GameTask() {
            @Override
            public void run() {
                if (!player.isHeldSlot(HotbarSlots.WEAPON)) {
                    entityPoof();
                    PlayerLib.playSound(Sound.ITEM_SHIELD_BREAK, 0.75f);
                    ((GravityGun) Heroes.DR_ED.getHero().getWeapon()).setElement(player, null);
                    player.sendMessage("&aYour current equipped element has shattered!");
                    cancel();
                    return;
                }

                player.sendSubtitle("&f[&a&l%s&f]".formatted(Chat.capitalize(type)), 0, 10, 0);
                entity.teleport(player.getLocation().add(player.getLocation().getDirection().multiply(2)));
            }
        }.runTaskTimer(0, 1);
    }

    public void stopTask() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    public Entity getEntity() {
        return entity;
    }

    private String getHeadTexture(Block block) {
        final Skull skull = (Skull) block.getState();
        try {
            final Field field = skull.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            final GameProfile profile = (GameProfile) field.get(skull);
            final Collection<Property> textures = profile.getProperties().get("textures");

            for (Property texture : textures) {
                return texture.value();
            }

            field.setAccessible(false);

        } catch (Exception exception) {
            return null;
        }
        return null;
    }

    private void entityPoof() {
        final Location fixedLocation = entity.getLocation().add(0.0d, 1.5d, 0.0d);
        PlayerLib.playSound(fixedLocation, this.material.createBlockData().getSoundGroup().getBreakSound(), 0.75f);
        PlayerLib.spawnParticle(fixedLocation, Particle.POOF, 3, 0.1d, 0.05d, 0.1d, 0.02f);
        entity.remove();
    }

    private Entity spawnBlockEntity(Location location) {
        return Entities.ARMOR_STAND.spawn(location, me -> {
            me.setMarker(true);
            me.setInvisible(true);
            if (me.getEquipment() != null) {
                me.getEquipment().setHelmet(this.stack);
            }
        });
    }

    private enum AnimationType {

        FULL_BLOCK(0.2d, 0.0d, 0.1d),
        SLAB(0.0d, 0.35d, 0.0d),
        STAIRS(0.25d, 0.0d, 0.0d);

        private final double x, y, z;

        AnimationType(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
