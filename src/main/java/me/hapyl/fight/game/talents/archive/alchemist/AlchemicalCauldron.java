package me.hapyl.fight.game.talents.archive.alchemist;

import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.alchemist.Alchemist;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Action;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;

public class AlchemicalCauldron extends TickingGameTask {

    private final Player owner;
    private final Location location;
    private final ArmorStand standBar;
    private final ArmorStand standOwner;
    private final ArmorStand standAnimation;

    private double progress;
    private Status status;
    private Block cauldronBlock;

    public AlchemicalCauldron(Player owner, Location location) {
        this.owner = owner;
        this.location = location;
        this.createCauldron();
        this.progress = 0.0d;
        this.status = Status.NEUTRAL;

        this.standOwner = createStand(location.clone().add(0.5d, 1.25d, 0.5d));
        this.standBar = createStand(location.clone().add(0.5d, 1.0d, 0.5d));
        this.standAnimation = createStand(location.clone().add(0.5, -0.75d, 0.5d), self -> {
            final EntityEquipment equipment = self.getEquipment();
            if (equipment == null) {
                return;
            }

            equipment.setItemInMainHand(ItemBuilder.of(Material.AIR).toItemStack());
            self.setRightArmPose(new EulerAngle(Math.toRadians(-85.0d), Math.toRadians(-90), 0));

            self.setSmall(false);
            self.setCustomNameVisible(false);
        });

        updateName();
        runTaskTimer(0, 1);
    }

    @Override
    public void run(final int tick) {
        if (progress > 100) {
            status = Status.FINISHED;
            playSound(Sound.BLOCK_BREWING_STAND_BREW, 1.0f);
            cancel();
        }

        updateName();

        if (status == Status.PAUSED || status == Status.FINISHED) {
            return;
        }

        if (status == Status.BREWING) {
            // Animate every tick but progress every 10
            animateCauldron();

            if (tick % 10 != 0) {
                return;
            }

            progress += 1.5d;

            if (progress % 15 == 0) {
                playSound(Sound.AMBIENT_UNDERWATER_EXIT, 1.5f);
            }

            // Draw particles on top
            spawnParticle(location);

            // Draw the zone
            Geometry.drawCircle(location, 4.5d, Quality.SUPER_HIGH, new Draw(Particle.SPELL) {
                @Override
                public void draw(Location location) {
                    spawnParticle(location);
                }
            });

            // Damage players in zone
            Collect.nearbyPlayers(location, 4.5d).forEach(player -> {
                if (player.is(owner)) {
                    player.sendSubtitle("&cIntoxication Warning!", 0, 20, 0);
                    ((Alchemist) Heroes.ALCHEMIST.getHero()).addToxin(player.getPlayer(), 8);
                }
                else {
                    player.addPotionEffect(PotionEffectType.POISON, 1, 5);
                }
            });
        }
    }

    private void animateCauldron() {
        final Location standLocation = this.standAnimation.getLocation();
        standLocation.setYaw(standLocation.getYaw() + 10);
        this.standAnimation.teleport(standLocation);
    }

    private void playSound(Sound sound, float pitch) {
        PlayerLib.playSound(location, sound, pitch);
    }

    private void spawnParticle(Location location) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.spawnParticle(Particle.SPELL_MOB, location.getX() + 0.5d, location.getY(), location.getZ() + 0.5d, 0, 0.000, 0.471, 0.031, 1);
    }

    // just check for the distance at this point
    public boolean compareBlock(Block other) {
        return this.location.distance(other.getLocation()) < 2.0d;
    }

    private ArmorStand createStand(Location location) {
        return createStand(location, null);
    }

    private ArmorStand createStand(Location location, Action<ArmorStand> action) {
        return Entities.ARMOR_STAND.spawn(location, me -> {
            me.setMarker(true);
            me.setSmall(true);
            me.setInvisible(true);
            me.setCustomNameVisible(true);
            if (action != null) {
                action.use(me);
            }
        });
    }

    public void setStatus(Status status) {
        if (owner.hasCooldown(Material.STICK)) {
            return;
        }
        this.status = status;

        if (status == Status.BREWING) {
            Nulls.runIfNotNull(standAnimation.getEquipment(), eq -> eq.setItemInMainHand(new ItemStack(Material.STICK)));
        }
        else if (status == Status.PAUSED) {
            Nulls.runIfNotNull(standAnimation.getEquipment(), eq -> eq.setItemInMainHand(new ItemStack(Material.AIR)));
        }
    }

    public Status getStatus() {
        return status;
    }

    public void updateName() {
        if (this.standBar == null || this.standOwner == null) {
            return;
        }
        this.standBar.setCustomName(Chat.format(
                status == Status.FINISHED ? status.getStatusString() : "&e%s%% %s",
                BukkitUtils.decimalFormat(progress),
                this.status.getStatusString()
        ));
        this.standOwner.setCustomName(Chat.format("&a%s's Cauldron", owner.getName()));
    }

    private void createCauldron() {
        cauldronBlock = this.location.getBlock();
        cauldronBlock.setType(Material.WATER_CAULDRON, false);

        if (cauldronBlock.getState() instanceof Levelled levelled) {
            levelled.setLevel(levelled.getMaximumLevel());
        }

        cauldronBlock.getState().update(true, false);
    }

    public void finish() {
        PlayerLib.addEffect(owner, PotionEffectType.SPEED, 30, 2);
        PlayerLib.playSound(owner, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 2.0f);
        Chat.sendMessage(owner, "&aYou have gained the Cauldron Buff!");
        Talents.CAULDRON.getTalent().startCd(owner);
        ((Alchemist) Heroes.ALCHEMIST.getHero()).startCauldronBoost(owner);
        clear();
    }

    public void clear() {
        //this.location.getBlock().setType(Material.AIR, false);
        cancelIfActive();
        cauldronBlock.setType(Material.AIR, false);
        standOwner.remove();
        standBar.remove();
        standAnimation.remove();
    }

    public enum Status {

        NEUTRAL(""),
        PAUSED("&e&lPAUSED"),
        BREWING("&a&lBREWING..."),
        FINISHED("&6&lFINISHED, CLICK TO COLLECT");

        private final String statusString;

        Status(String statusString) {
            this.statusString = statusString;
        }

        public String getStatusString() {
            return statusString;
        }

        public boolean shouldTakeStick() {
            return this != BREWING && this != FINISHED;
        }

    }

}
