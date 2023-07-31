package me.hapyl.fight.game.heroes.archive.juju;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.juju.ArrowShield;
import me.hapyl.fight.game.talents.archive.juju.TricksOfTheJungle;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Iterators;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class JuJu extends Hero implements Listener, UIComplexComponent, HeroPlaque {

    private final Map<Player, ArrowData> playerArrows = Maps.newHashMap();
    private final Map<Arrow, ArrowType> arrowType = Maps.newHashMap();

    private final Set<Player> climbing = Sets.newHashSet();
    private final int CLIMB_COOLDOWN = Tick.fromSecond(10);

    public JuJu() {
        super("Juju");

        setRole(Role.RANGE);
        setArchetype(Archetype.HEXBANE);

        setMinimumLevel(5);

        setDescription("A bandit from the depths of the jungle. Highly skilled in range combat.");
        setItem("9dcff46588f394987979b7dd770adea94d8ee1fb1f7b8704e1baf91227f6a4d");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(62, 51, 40);
        equipment.setLeggings(62, 51, 40);
        equipment.setBoots(16, 13, 10);

        setWeapon(new Weapon(Material.BOW)
                .setName("Twisted")
                .setDescription("A bow made of anything you can find in the middle of the jungle.")
                .setDamage(4.0d));

        setUltimate(
                new UltimateTalent(ArrowType.POISON_IVY.getName(), 60)
                        .setItem(Material.SPIDER_EYE)
                        .setDurationSec(4),
                then -> then.setDescription(ArrowType.POISON_IVY.getTalentDescription(then))
        );
    }

    public void setArrowType(Player player, ArrowType type, int duration) {
        final ArrowData arrowData = playerArrows.get(player);

        if (arrowData != null) {
            Message.error(player, "Cannot change now!");
            return;
        }

        final ArrowData previous = playerArrows.put(player, new ArrowData(player, type, duration));

        if (previous != null) {
            previous.cancelIfActive();
            previous.type.onUnequip(player);
        }

        type.onEquip(player);
    }

    public void unequipArrow(Player player, ArrowType type) {
        final ArrowData data = playerArrows.remove(player);

        if (data == null || data.type != type) {
            return;
        }

        data.cancel();
        data.type.onUnequip(player);
        playerArrows.remove(player);
    }

    @EventHandler()
    public void handleJumping(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();

        if (!validatePlayer(player)) {
            return;
        }

        final Location from = ev.getFrom();
        final Location to = ev.getTo();

        if (player.hasCooldown(getPassiveTalent().getMaterial())
                || climbing.contains(player)
                || to == null
                || to.getY() <= from.getY()
                || player.isOnGround()) { // I mean just don't hack lol
            return;
        }

        final boolean huggingWall = isHuggingWall(player);

        // Have to check for 2 blocks at the
        // start to avoid annoying 1 block climbing.
        if (!huggingWall || !isHuggingWall(player.getEyeLocation())) {
            return;
        }

        climbing.add(player);
    }

    @EventHandler()
    public void handleSneaking(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();

        if (!ev.isSneaking() || !climbing.contains(player)) {
            return;
        }

        final Location location = player.getLocation();
        final Vector vector = location.getDirection().normalize().multiply(0.25d).setY(0.3d);

        player.setVelocity(vector);

        // Fx
        PlayerLib.playSound(player, Sound.BLOCK_LADDER_STEP, 0.0f);

        if (new Random().nextBoolean()) {
            player.swingMainHand();
        }
        else {
            player.swingOffHand();
        }
    }

    @Override
    public boolean predicateUltimate(Player player) {
        return getArrowType(player) == null;
    }

    @Override
    public String predicateMessage(Player player) {
        return "Cannot use while " + getSecondTalent().getName() + " is active!";
    }

    @Override
    public void useUltimate(Player player) {
        setArrowType(player, ArrowType.POISON_IVY, getUltimateDuration());
    }

    @EventHandler()
    public void handleBowShoot(ProjectileLaunchEvent ev) {
        final Projectile projectile = ev.getEntity();

        if (!(projectile instanceof Arrow arrow)
                || !(arrow.getShooter() instanceof Player player)) {
            return;
        }

        final ArrowData arrowData = playerArrows.get(player);

        if (arrowData == null) {
            return;
        }

        arrowData.type.onShoot(player, arrow);
        arrowType.put(arrow, arrowData.type);
    }

    @EventHandler()
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        final Projectile projectile = ev.getEntity();

        if (!(projectile instanceof Arrow arrow) || !(projectile.getShooter() instanceof Player player)) {
            return;
        }

        final ArrowType arrowType = this.arrowType.get(arrow);

        if (arrowType != null) {
            arrowType.onHit(player, arrow);
        }
    }

    @Override
    public void onDeath(Player player) {
        climbing.remove(player);
        final ArrowData data = playerArrows.remove(player);

        if (data != null) {
            data.cancelIfActive();
        }

        // Remove all player-owned arrows
        arrowType.keySet().removeIf(arrow -> arrow.getShooter() == player);
    }

    @Override
    public void onStop() {
        climbing.clear();

        playerArrows.values().forEach(GameTask::cancel);
        playerArrows.clear();

        arrowType.keySet().forEach(Arrow::remove);
        arrowType.clear();
    }

    @Override
    public void onStart(Player player) {
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
    }

    @Nullable
    @Override
    public List<String> getStrings(Player player) {
        final ArrowType type = getArrowType(player);
        final int climbCooldown = player.getCooldown(getPassiveTalent().getMaterial());

        return List.of(
                climbCooldown <= 0 ? "" : "&7&l🧗 " + Tick.round(climbCooldown) + "s",
                type == null ? "" : "&a🎯 &l" + type.getName()
        );
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                // Climbing
                Iterators.iterate(climbing, (remover, player) -> {
                    if (!isHuggingWall(player)) {
                        remover.remove();
                        player.setCooldown(getPassiveTalent().getMaterial(), CLIMB_COOLDOWN);
                        return;
                    }

                    player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(2, 1));
                });

                // Arrows
                arrowType.keySet().removeIf(Arrow::isDead);
                arrowType.forEach((arrow, type) -> {
                    // should never ever happen but just in case
                    if (arrow.getShooter() instanceof Player player) {
                        type.onTick(player, arrow);
                    }
                });
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final ArrowShield shield = getFirstTalent();
        final Player player = input.getBukkitPlayer();

        if (shield.getCharges(player) > 0) {
            shield.removeCharge(player);

            return DamageOutput.CANCEL;
        }

        return null;
    }

    @Override
    @Nonnull
    public ArrowShield getFirstTalent() {
        return (ArrowShield) Talents.ARROW_SHIELD.getTalent();
    }

    @Override
    @Nonnull
    public TricksOfTheJungle getSecondTalent() {
        return (TricksOfTheJungle) Talents.TRICKS_OF_THE_JUNGLE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.JUJU_PASSIVE.getTalent();
    }

    @Nonnull
    @Override
    public String text() {
        return "&b&lUPDATED!";
    }

    @Nullable
    public ArrowData getArrowData(Player player) {
        return playerArrows.get(player);
    }

    @Nullable
    public ArrowType getArrowType(Player player) {
        final ArrowData data = getArrowData(player);
        return data != null ? data.type : null;
    }

    private Arrow bounceArrow(Arrow arrow, BlockFace hitBlock) {
        // Calculate the angle of reflection for the arrow.
        final Vector arrowVelocity = arrow.getVelocity();
        final Vector surfaceNormal = hitBlock.getDirection();
        final Vector reflectedVelocity = arrowVelocity.subtract(surfaceNormal.multiply(1.5d * arrowVelocity.dot(surfaceNormal)));

        // Set the new velocity of the arrow to the reflected velocity.
        return Entities.ARROW.spawn(arrow.getLocation(), self -> {
            self.setShooter(arrow.getShooter());
            self.setDamage(arrow.getDamage() * 2);
            self.setColor(Color.GREEN);
            self.setCritical(arrow.isCritical());
            self.setVelocity(reflectedVelocity);
        });
    }

    private boolean isHuggingWall(Location location) {
        final Vector direction = location.getDirection();
        final Location inFront = location.add(direction.normalize().setY(0.0d).multiply(1.0d));

        return inFront.getBlock().getType().isOccluding();
    }

    private boolean isHuggingWall(Player player) {
        return isHuggingWall(player.getLocation());
    }

}
