package me.hapyl.fight.game.heroes.archer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.ProjectilePostLaunchEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.EquipmentSlots;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.skin.archer.AbstractSkinArcher;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.archer.HawkeyePassive;
import me.hapyl.fight.game.talents.archer.ShockDart;
import me.hapyl.fight.game.talents.archer.TripleShot;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.game.weapons.BowWeapon;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.ProgressBarBuilder;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class Archer extends Hero implements Listener, PlayerDataHandler<ArcherData> {

    protected final Weapon boomBow = Weapon.builder(Material.BOW, Key.ofString("boom_bow"))
            .damage(1.0)
            .name("&6&lBOOM BOW!")
            .build();

    private final Set<Arrow> boomArrows = new HashSet<>();
    private final PlayerDataMap<ArcherData> playerData;

    private final double explosionRadius = 3.0d;
    private final double explosionDamage = 40.0d;

    private final int boomBowPerShotCd = 5;

    private final Color hawkeyeArrowColors = Color.fromRGB(19, 81, 143);

    public Archer(@Nonnull Key key) {
        super(key, "Archer");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.RANGE, Archetype.TALENT_DAMAGE, Archetype.POWERFUL_ULTIMATE);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.HUMAN);

        setDescription("One of the best archers joins the fight! Not alone though but with his &3&ocustom-made &8&obow.");
        setItem("106c16817c73ff64a4a49b590d2cdb25bcfa52c630fe7281a177eabacdaa857b");

        setWeapon(BowWeapon
                .of(Key.ofString("destiny_bow"), "Bow of Destiny", "A named-made bow with some unique abilities!", 5.0d)
                .setShotCooldown(7)
        );

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.MAX_HEALTH, 100.0d);
        attributes.set(AttributeType.SPEED, 0.23d);
        attributes.set(AttributeType.DEFENSE, 0.7d);

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(86, 86, 87);
        equipment.setLeggings(75, 75, 87);
        equipment.setBoots(51, 51, 51);

        setMastery(new ArcherMastery(this));

        final ArcherUltimate ultimate = new ArcherUltimate();

        playerData = PlayerMap.newDataMap(player -> new ArcherData(player, ultimate));
        setUltimate(ultimate);
    }

    @Nonnull
    @Override
    public ArcherMastery getMastery() {
        return CFUtils.castNullable(this.mastery, ArcherMastery.class);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlots.ARROW, new ItemStack(Material.ARROW));
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        CFUtils.clearCollection(boomArrows);
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                boomArrows.forEach(arrow -> {
                    if (arrow.isDead()) {
                        return;
                    }

                    final Location location = arrow.getLocation();

                    if (!(arrow.getShooter() instanceof Player player)) {
                        return;
                    }

                    final GamePlayer gamePlayer = CF.getPlayer(player);

                    if (gamePlayer == null) {
                        return;
                    }

                    if (gamePlayer.runSkin(AbstractSkinArcher.class, skin -> skin.boomArrowTick(gamePlayer, location))) {
                        return;
                    }

                    gamePlayer.spawnWorldParticle(location, Particle.FLAME, 2, 0, 0, 0, 0.015f);
                    gamePlayer.playWorldSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2.0f);
                });
            }
        }.runTaskTimer(0, 2);
    }

    @EventHandler()
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && boomArrows.contains(arrow)) {
            final ProjectileSource shooter = arrow.getShooter();

            if (shooter instanceof Player player) {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    return;
                }

                gamePlayer.createExplosion(arrow.getLocation(), explosionRadius, explosionDamage, DamageCause.BOOM_BOW);
            }
        }
    }

    @EventHandler()
    public void handleProjectileLaunchEvent(ProjectilePostLaunchEvent ev) {
        if (ev.getProjectile() instanceof Arrow arrow) {
            // Handle ultimate arrows
            final Color color = arrow.getColor();
            final GamePlayer player = ev.getShooter();

            if (!validatePlayer(player)) {
                return;
            }

            if (player.isUsingUltimate() && color == null) {
                boomArrows.add(arrow);

                player.cooldownManager.setCooldown(boomBow, boomBowPerShotCd);

                // Decrement fuse
                final ArcherData data = getPlayerDataOrNull(player);

                if (data != null) {
                    data.decrementFuse();
                }

                return;
            }

            // Handle hawkeye arrows
            if (!player.isHeldSlot(HotBarSlot.WEAPON) || !arrow.isCritical() || !player.isSneaking()) {
                return;
            }

            final ArcherMastery mastery = getMastery();
            final double passiveChance = mastery.getPassiveChance(player, getPassiveTalent().chance);

            if (!player.random.checkBound(1 - passiveChance)) {
                return;
            }

            arrow.setColor(
                    player.getSkinValue(AbstractSkinArcher.class, AbstractSkinArcher::getHawkeyeArrowColor, hawkeyeArrowColors)
            );

            new GameTask() {
                private int tick;

                @Override
                public void run() {
                    if (arrow.isDead()) {
                        this.cancel();
                        return;
                    }

                    ++tick;

                    final Entity target = findHomingTarget(player, arrow.getLocation());

                    // Fx
                    final Location location = arrow.getLocation();

                    if (tick % 2 == 0) {
                        if (!player.runSkin(AbstractSkinArcher.class, skin -> skin.hawkeyeArrowTick(player, location))) {
                            player.spawnWorldParticle(location, Particle.ENCHANTED_HIT, 5, 0, 0, 0, 0);
                            player.playWorldSound(location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 2.0f);
                        }
                    }

                    if (target == null) {
                        return;
                    }

                    final Vector vector = target.getLocation()
                            .add(0.0d, 0.5d, 0.0d)
                            .toVector()
                            .subtract(arrow.getLocation().toVector())
                            .normalize()
                            .multiply(0.7d);

                    arrow.setVelocity(vector);

                }
            }.runTaskTimer(0, 1);

            // Fx
            player.playSound(Sound.ENCHANT_THORNS_HIT, 2.0f);
            player.playSound(Sound.ENTITY_ELDER_GUARDIAN_DEATH_LAND, 1.25f);
        }
    }

    @Override
    public TripleShot getFirstTalent() {
        return TalentRegistry.TRIPLE_SHOT;
    }

    @Override
    public ShockDart getSecondTalent() {
        return TalentRegistry.SHOCK_DART;
    }

    @Override
    public HawkeyePassive getPassiveTalent() {
        return TalentRegistry.HAWKEYE_ARROW;
    }

    @Nonnull
    @Override
    public PlayerDataMap<ArcherData> getDataMap() {
        return playerData;
    }

    @Nonnull
    @Override
    public ArcherUltimate getUltimate() {
        return (ArcherUltimate) super.getUltimate();
    }

    private Entity findHomingTarget(GamePlayer shooter, Location location) {
        final LivingGameEntity gameEntity = Collect.nearestEntity(location, getPassiveTalent().homingRadius, shooter);

        return gameEntity == null ? null : gameEntity.getEntity();
    }

    public class ArcherUltimate extends UltimateTalent {

        @DisplayField protected final short baseFuse = 100;
        @DisplayField protected final short fuseShotCost = 20;

        public ArcherUltimate() {
            super(Archer.this, "Boom Bow", 70);

            setDescription("""
                    Light the &6fuse&7 and equip a &6&lBOOM BOW&7 that shoots explosive arrows that &cexplode&7 on impact, dealing massive %s&7.
                    """.formatted(EnumTerm.TRUE_DAMAGE));

            setItem(Material.BLAZE_POWDER);
            setSound(Sound.ITEM_CROSSBOW_SHOOT, 0.25f);

            setManualDuration();
            setCooldownSec(20);

            addAttributeDescription("Explosion Radius", explosionRadius + " blocks");
            addAttributeDescription("Explosion Damage", explosionDamage);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
                player.setUsingUltimate(true);

                player.setItemAndSnap(HotBarSlot.HERO_ITEM, boomBow.getItem());
                player.cooldownManager.setCooldown(boomBow, boomBowPerShotCd);

                final ProgressBarBuilder progressBuild = new ProgressBarBuilder("\uD83D\uDD25", ChatColor.GOLD, 10);
                final ArcherData playerData = getPlayerData(player);

                final float maxFuse = getMastery().getMaxFuse(player, baseFuse);
                playerData.fuse = maxFuse;

                new PlayerTickingGameTask(player) {
                    @Override
                    public void run(int tick) {
                        if (playerData.fuse <= 0) {
                            player.setItem(HotBarSlot.HERO_ITEM, null);
                            player.snapToWeapon();
                            player.setUsingUltimate(false);

                            removePlayerData(player);
                            cancel();
                            return;
                        }

                        // Display fuse
                        player.sendTitle(" ", progressBuild.build((int) playerData.fuse, (int) maxFuse), 0, 5, 0);

                        playerData.fuse--;
                    }
                }.runTaskTimer(0, 1);
            });
        }
    }

}
