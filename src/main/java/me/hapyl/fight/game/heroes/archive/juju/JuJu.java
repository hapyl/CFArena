package me.hapyl.fight.game.heroes.archive.juju;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.ProjectilePostLaunchEvent;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.EquipmentSlot;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.juju.ArrowShield;
import me.hapyl.fight.game.talents.archive.juju.TricksOfTheJungle;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.BowWeapon;
import me.hapyl.fight.util.Iterators;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class JuJu extends Hero implements Listener, UIComplexComponent {

    private final PlayerMap<ArrowData> playerArrows = PlayerMap.newMap();
    private final Map<Arrow, ArrowType> arrowType = Maps.newHashMap();

    private final Set<GamePlayer> climbing = Sets.newHashSet();
    private final int CLIMB_COOLDOWN = Tick.fromSecond(10);

    public JuJu(@Nonnull Heroes handle) {
        super(handle, "Juju");

        setArchetype(Archetype.HEXBANE);
        setAffiliation(Affiliation.THE_JUNGLE);
        setGender(Gender.MALE);

        setMinimumLevel(5);

        setDescription("A bandit from the depths of the jungle. Highly skilled in range combat.");
        setItem("9dcff46588f394987979b7dd770adea94d8ee1fb1f7b8704e1baf91227f6a4d");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(62, 51, 40);
        equipment.setLeggings(62, 51, 40);
        equipment.setBoots(16, 13, 10);

        setWeapon(new BowWeapon()
                .setName("Twisted")
                .setDescription("A bow made of anything you can find in the middle of the jungle.")
                .setDamage(4.0d));

        setUltimate(new JujuUltimate());
    }

    public void setArrowType(GamePlayer player, ArrowType type, int duration) {
        final ArrowData arrowData = playerArrows.get(player);

        if (arrowData != null) {
            player.sendMessage("Cannot change now!");
            return;
        }

        final ArrowData previous = playerArrows.put(player, new ArrowData(player, type, duration));

        if (previous != null) {
            previous.cancel();
            previous.type.onUnequip(player);
        }

        type.onEquip(player);
    }

    public void unequipArrow(GamePlayer player, ArrowType type) {
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

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        final Location from = ev.getFrom();
        final Location to = ev.getTo();

        if (player.hasCooldown(getPassiveTalent().getMaterial())
                || climbing.contains(gamePlayer)
                || to == null
                || to.getY() <= from.getY()
                || gamePlayer.isOnGround()) { // I mean just don't hack lol
            return;
        }

        final boolean huggingWall = isHuggingWall(gamePlayer);

        // Have to check for 2 blocks at the
        // start to avoid annoying 1 block climbing.
        if (!huggingWall || !isHuggingWall(player.getEyeLocation())) {
            return;
        }

        climbing.add(gamePlayer);
    }

    @EventHandler()
    public void handleSneaking(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();
        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        if (!ev.isSneaking() || !climbing.contains(gamePlayer)) {
            return;
        }

        final Location location = player.getLocation();
        final Vector vector = location.getDirection().normalize().multiply(0.25d).setY(0.3d);

        player.setVelocity(vector);

        // Fx
        gamePlayer.playWorldSound(Sound.BLOCK_LADDER_STEP, 0.0f);

        if (new Random().nextBoolean()) {
            player.swingMainHand();
        }
        else {
            player.swingOffHand();
        }
    }

    @EventHandler()
    public void handleBowShoot(ProjectilePostLaunchEvent ev) {
        final Projectile projectile = ev.getProjectile();

        if (!(projectile instanceof Arrow arrow) || !(ev.getShooter() instanceof GamePlayer player)) {
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

        final GamePlayer gamePlayer = CF.getPlayer(player);
        final ArrowType arrowType = this.arrowType.get(arrow);

        if (gamePlayer != null && arrowType != null) {
            arrowType.onHit(gamePlayer, arrow);
        }
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        climbing.remove(player);
        final ArrowData data = playerArrows.remove(player);

        if (data != null) {
            data.cancel();
        }

        // Remove all player-owned arrows
        arrowType.keySet().removeIf(arrow -> {
            final ProjectileSource shooter = arrow.getShooter();
            return shooter instanceof Player shooterPlayer && player.is(shooterPlayer);
        });
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
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlot.ARROW, new ItemStack(Material.ARROW));
    }

    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
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

                        // Add a little boost
                        final Location location = player.getLocation();
                        final Vector vector = location.getDirection().normalize().multiply(0.8d).add(new Vector(0.0d, 0.25d, 0.0d));

                        player.setVelocity(vector);
                        player.addEffect(Effects.SLOW_FALLING, 1, 10);
                        player.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 100);

                        // Fx
                        player.playWorldSound(Sound.ENTITY_HORSE_SADDLE, 0.75f);
                        return;
                    }

                    player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(2, 1));
                });

                // Arrows
                arrowType.keySet().removeIf(Arrow::isDead);
                arrowType.forEach((arrow, type) -> {
                    // should never ever happen but just in case
                    if (arrow.getShooter() instanceof Player player) {
                        final GamePlayer gamePlayer = CF.getPlayer(player);
                        if (gamePlayer == null) {
                            return;
                        }

                        type.onTick(gamePlayer, arrow);
                    }
                });
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final ArrowShield shield = getFirstTalent();
        final GamePlayer player = instance.getEntityAsPlayer();

        if (shield.getCharges(player) > 0) {
            shield.removeCharge(player);

            instance.setCancelled(true);
        }
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

    @Nullable
    public ArrowData getArrowData(GamePlayer player) {
        return playerArrows.get(player);
    }

    @Nullable
    public ArrowType getArrowType(GamePlayer player) {
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
        final Location inFront = location.add(direction.normalize().setY(0.0d));

        return inFront.getBlock().getType().isOccluding();
    }

    private boolean isHuggingWall(GamePlayer player) {
        return isHuggingWall(player.getLocation().add(0, player.getEyeHeight() / 2, 0));
    }

    private class JujuUltimate extends UltimateTalent {
        public JujuUltimate() {
            super(ArrowType.POISON_IVY.getName(), 60);

            setDescription(ArrowType.POISON_IVY.getTalentDescription(this));

            setType(Talent.Type.IMPAIR);
            setItem(Material.SPIDER_EYE);
            setDurationSec(4);

            copyDisplayFieldsFrom(Talents.POISON_ZONE.getTalent());
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final ArrowType type = getArrowType(player);

            if (type != null) {
                return UltimateResponse.error("Already using %s!".formatted(type.getName()));
            }

            setArrowType(player, ArrowType.POISON_IVY, getUltimateDuration());
            player.snapToWeapon();

            return UltimateResponse.OK;
        }
    }
}
