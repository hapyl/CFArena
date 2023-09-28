package me.hapyl.fight.game.talents.archive.bloodfiend.chalice;

import me.hapyl.fight.fx.SwiftTeleportAnimation;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.archive.bloodfiend.taunt.Taunt;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class BloodChalice extends Taunt implements TalentReference<BloodChaliceTalent> {

    private static final ItemStack CHALICE_TEXTURE
            = ItemBuilder.playerHeadUrl("492f08c1294829d471a8e0109a06fb6ae717e5faf3e0808408a66d889227dac7").asIcon();
    private static final ItemStack BLOOD_TEXTURE
            = ItemBuilder.playerHeadUrl("c0340923a6de4825a176813d133503eff186db0896e32b6704928c2a2bf68422").asIcon();

    private final BloodChaliceTalent reference;
    private final ArmorStand[] stand;
    private int health;

    public BloodChalice(BloodChaliceTalent reference, Player player, GamePlayer target) {
        super(player, target);
        final Location location = player.getLocation();

        this.health = reference.chaliceHealth;
        this.reference = reference;
        this.stand = new ArmorStand[2];

        this.stand[0] = spawnEntity(Entities.ARMOR_STAND, location, self -> {
            CFUtils.lockArmorStand(self);

            self.setMaxHealth(reference.chaliceHealth);
            self.setHealth(reference.chaliceHealth);
            self.setHelmet(CHALICE_TEXTURE);
            self.setGravity(false);
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setSilent(true);
        });
    }

    @Override
    public void onAnimationStep(Location location) {
        location.setYaw(location.getYaw() + 15);
        stand[0].teleport(location);
    }

    @Override
    public void onAnimationEnd() {
        this.initialLocation.add(0.0d, 1.5d, 0.0d);

        this.stand[0].setInvulnerable(false);
        this.stand[1] = spawnEntity(Entities.ARMOR_STAND, initialLocation, self -> {
            self.setMarker(true);
            self.setSmall(true);
            self.setInvisible(true);
            self.setSilent(true);
            self.setHelmet(BLOOD_TEXTURE);

            CFUtils.setGlowing(player, self, "chalice", ChatColor.GREEN);
            CFUtils.setGlowing(target.getPlayer(), self, "chalice", ChatColor.RED);

            self.setGlowing(true);
        });

        start(reference.getDuration());
    }

    @Override
    public void run(int tick) {
        final int timeLeft = getTimeLeft();
        final Location baseLocation = stand[0].getLocation();

        if (player.getLocation().distance(baseLocation) >= reference.maxDistance) {
            remove();

            Chat.sendMessage(player, "&4&l🍷 &cThe Chalice broke because you strayed too far away!");
            return;
        }

        if (stand[0].isDead()) {
            remove();
            return;
        }

        stand[0].setCustomName(Chat.format("&bTaunting %s &c%s", target.getName(), CFUtils.decimalFormatTick(timeLeft)));
        stand[0].setCustomNameVisible(true);

        final Location location = stand[1].getLocation();

        // Fx
        final double baseY = baseLocation.getY() + 1.5d;
        final double radians = Math.toRadians(tick * 2);
        final double y = Math.sin(radians) / 2;

        baseLocation.setY(baseY + y);

        location.setYaw(location.getYaw() + 5);
        baseLocation.setYaw(location.getYaw());

        stand[1].teleport(baseLocation);

        asPlayers(player -> {
            player.spawnParticle(
                    Particle.REDSTONE,
                    baseLocation.add(0.0d, 0.76d, 0.0d),
                    1,
                    0.2d,
                    0.2d,
                    0.2d,
                    new Particle.DustOptions(Color.RED, 1)
            );
        });
    }

    @Nonnull
    @Override
    public String getName() {
        return reference.getName();
    }

    @Nonnull
    @Override
    public String getCharacter() {
        return "&4&l🍷";
    }

    @Override
    public double getDamage() {
        return reference.getExplosionDamage();
    }

    @Nonnull
    @Override
    public EnumDamageCause getDamageCause() {
        return reference.getDamageCause();
    }

    @Override
    public void onTaskStop() {
        reference.removeTaunt(player);
    }

    @Override
    public void remove() {
        super.remove();

        final Location location = stand[0].getLocation().add(0.0d, 1.5d, 0.0d);

        CFUtils.forEach(stand, Entity::remove);

        // Fx
        asPlayers(player -> {
            PlayerLib.playSound(player, location, Sound.BLOCK_GLASS_BREAK, 0.0f);
            PlayerLib.spawnParticle(player, location, Particle.EXPLOSION_NORMAL, 15, 0.1d, 0.1d, 0.1d, 0.05f);
        });
    }

    @Nonnull
    @Override
    public BloodChaliceTalent getTalent() {
        return reference;
    }

    public boolean isPart(Entity entity) {
        if (entity == null) {
            return false;
        }

        for (ArmorStand armorStand : stand) {
            if (armorStand == entity) {
                return true;
            }
        }

        return false;
    }

    public void crack() {
        stand[0].setNoDamageTicks(20);
        health--;

        asPlayers(player -> {
            PlayerLib.playSound(player, initialLocation, Sound.ENTITY_SKELETON_HURT, 0.0f);
        });

        if (health <= 0) {
            remove();
            return;
        }

        final Location location = pickRandomLocation(5);
        final Location currentLocation = this.initialLocation.clone();
        this.initialLocation = location.subtract(0.0d, 1.3d, 0.0d);

        final SwiftTeleportAnimation animation
                = new SwiftTeleportAnimation(currentLocation, this.initialLocation) {
            @Override
            public void onAnimationStep(Location location) {
                final ArmorStand stand = BloodChalice.this.stand[0];

                if (stand.isDead()) {
                    cancelIfActive();
                    return;
                }

                stand.teleport(location);
            }
        };

        animation.setSpeed(2.0d);
        animation.setSlope(Math.PI / 2);
        animation.start(0, 1);
    }
}
