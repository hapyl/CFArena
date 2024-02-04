package me.hapyl.fight.game.heroes.archive.rogue;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.rogue.SecondWind;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Rogue extends Hero implements PlayerDataHandler<RogueData>, UIComponent, DisplayFieldProvider {

    private final PlayerDataMap<RogueData> rogueData = PlayerMap.newDataMap(RogueData::new);

    private final double shieldCapacity = 30.0d;

    private final int passiveDuration = Tick.fromSecond(6);

    /**
     * A hack used to allow for faster attacks.
     * Increase for slower attacks.
     * The effect will be the same if the value is == 10
     */
    private final int attackSpeedTick = 6;

    private final TemperInstance temperInstance = Temper.SECOND_WIND.newInstance()
            .increase(AttributeType.ATTACK, 1.32d)
            .decrease(AttributeType.COOLDOWN_MODIFIER, 0.5d);

    @DisplayField private final double explosionRadius = 4.0d;
    @DisplayField private final double explosionDamage = 30.0d;
    @DisplayField private final int explosionDelay = 15;
    @DisplayField private final double magnitude = 0.53d;
    @DisplayField private final double yMagnitude = 0.33d;
    @DisplayField private final int bleedDuration = 60;

    public Rogue(@Nonnull Heroes handle) {
        super(handle, "Rogue");

        setArchetype(Archetype.DAMAGE);
        setAffiliation(Affiliation.MERCENARY);

        setDescription("""
                The most selfish member of the mercenaries.
                """);

        setItem("73abc6192f1a559ed566e50fddf6a7b50c42cb0a15862091411487ace1d60ab8");

        final HeroAttributes attributes = getAttributes();

        attributes.setHealth(60);
        attributes.setSpeed(130);
        attributes.setInfiniteAttackSpeed();

        final Equipment equipment = getEquipment();

        equipment.setChestPlate(Material.NETHERITE_CHESTPLATE, TrimPattern.WARD, TrimMaterial.NETHERITE);
        equipment.setLeggings(36, 14, 4, TrimPattern.DUNE, TrimMaterial.NETHERITE);
        equipment.setBoots(23, 7, 0, TrimPattern.SILENCE, TrimMaterial.NETHERITE);

        setWeapon(
                new Weapon(Material.GOLDEN_SWORD)
                        .setName("Sacrificial Dagger")
                        .setDescription("""
                                An ornate ceremonial dagger.
                                                                
                                Its small size allows for fast swings.
                                """)
                        .setDamage(3.0d)
        );

        setUltimate(new UltimateTalent(this, "Pipe Bomb", """
                Toss a hand-made pipe bomb in front of you that &4explodes&7 after a &bshort fuse&7, dealing &cdamage&7 and applying &cbleeding&7.
                                
                Also refresh %s charges.
                """.formatted(Named.SECOND_WIND), 60)
                .setItem(Material.LIGHTNING_ROD)
                .setSound(Sound.ENTITY_CREEPER_PRIMED, 1.0f)
        );

        copyDisplayFieldsToUltimate();
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();

        if (!instance.isDamageLethal()) {
            return;
        }

        final RogueData playerData = getPlayerData(player);

        if (playerData.secondWindCharges < 1) {
            return;
        }

        playerData.secondWindCharges--;

        instance.setCancelled(true);
        enterSecondWind(player);
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final LivingGameEntity entity = instance.getEntity();
        final LivingGameEntity damager = instance.getDamager();
        final EnumDamageCause cause = instance.getCause();

        // Only replace normal attacks
        if (cause != EnumDamageCause.ENTITY_ATTACK) {
            return;
        }

        instance.setCancelled(true);
        entity.damageTick(instance.getInitialDamage(), damager, EnumDamageCause.ROGUE_ATTACK, attackSpeedTick);
    }

    @Nullable
    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final World world = player.getWorld();
        final Location location = player.getMidpointLocation();
        final Item item = world.dropItem(location, new ItemStack(Material.LIGHTNING_ROD));

        item.setPickupDelay(10000);
        item.setUnlimitedLifetime(true);
        item.setVelocity(location.getDirection().normalize().multiply(magnitude).setY(yMagnitude));

        // Explode
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick > explosionDelay) {
                    explode();
                    return;
                }

                // Fx
                final Location location = item.getLocation();

                player.playWorldSound(location, Sound.BLOCK_NOTE_BLOCK_HAT, 0.5f + (1.0f / explosionDelay * tick));
                player.spawnWorldParticle(location, Particle.CRIT, 1);
            }

            private void explode() {
                final Location location = item.getLocation();

                Collect.nearbyEntities(location, explosionRadius).forEach(entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }

                    entity.damageNoKnockback(explosionDamage, player, EnumDamageCause.PIPE_BOMB);
                    entity.addEffect(Effects.BLEED, bleedDuration);
                });

                // Fx
                player.playWorldSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.25f);
                Geometry.drawSphere(location, explosionRadius * 3, explosionRadius,
                        loc -> player.spawnWorldParticle(loc, Particle.WAX_ON, 1, 0, 0, 0, 5)
                );

                item.remove();
                cancel();
            }
        }.runTaskTimer(0, 1);

        // Refresh passive
        getPlayerData(player).refreshSecondWindCharges();

        return UltimateCallback.OK;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.EXTRA_CUT.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SWAYBLADE.getTalent();
    }

    @Override
    public SecondWind getPassiveTalent() {
        return (SecondWind) Talents.SECOND_WIND.getTalent();
    }

    @Nonnull
    @Override
    public PlayerDataMap<RogueData> getDataMap() {
        return rogueData;
    }

    public void enterSecondWind(@Nonnull GamePlayer player) {
        final Shield shield = new Shield(player, shieldCapacity) {
            @Override
            public void onBreak() {
                player.die(true);
            }
        };

        player.setShield(shield);
        player.setOutline(Outline.RED);

        temperInstance.temper(player, passiveDuration);
        player.schedule(() -> {
            // If the state ended and still have shield, heal.
            if (player.getShield() == shield) {
                final double capacity = shield.getCapacity();

                player.heal(capacity * getPassiveTalent().passiveHealing);
                player.setOutline(Outline.CLEAR);
                player.setShield(null);

                // Fx
                player.playWorldSound(Sound.ENTITY_PLAYER_LEVELUP, 2.0f);
            }
        }, passiveDuration);

        // Fx
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.25f);
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.75f);

        player.spawnWorldParticle(Particle.TOTEM, 15, 0.1, 0.3, 0.1, 0.75f);

        //player.playEffect(EntityEffect.TOTEM_RESURRECT); Too obstructive
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final String character = Named.SECOND_WIND.getCharacter();
        final int charges = getPlayerData(player).secondWindCharges;

        return charges == 1 ? character : "&8" + Named.SECOND_WIND.getCharacter();
    }

}
